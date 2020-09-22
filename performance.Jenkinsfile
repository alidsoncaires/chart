def JMETER = [:]

def getOptions(def option){
    str = ""
    str += "def projetos = ["
    str += "[projeto:'teste.jmx', rotas:['Timeline - Cartões', 'Timeline - Conta Corrente']],"
    str += "[projeto:'teste2.jmx', rotas:['Cartões - Gastos por Categoria', 'Cartões - Balanço']]"
    str += "]\n"

    if(option=='PROJETO')
        str += "return projetos.with{it.projeto}"
    if(option=='ROTAS')
        str += "return projetos.findAll{it.projeto==PROJETO}.with{it.rotas}.first()"

    return str
}
//Definindo parametros dinamicos 
properties([
    parameters(
        [
            [$class: 'ChoiceParameter', choiceType: 'PT_SINGLE_SELECT', description: '', filterLength: 1, filterable: false, name: 'PROJETO', randomName: 'choice-parameter-249253690064314', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [classpath: [], sandbox: true, script: getOptions('PROJETO')]]], 
            [$class: 'CascadeChoiceParameter', choiceType: 'PT_CHECKBOX', description: '', filterLength: 1, filterable: false, name: 'ROTAS', randomName: 'choice-parameter-249253693373657', referencedParameters: 'PROJETO', script: [$class: 'GroovyScript', fallbackScript: [classpath: [], sandbox: false, script: ''], script: [classpath: [], sandbox: true, script: getOptions('ROTAS')]]]
        ]
    )
])

//Pipeline
pipeline {
    agent any
    //triggers {
    //    cron('H */4 * * 1-5')
    //}
    environment { 

        def JMETER_HOME = '/Users/alidsoncaires/Downloads/apache-jmeter-5.3'
        def PROJETO_HOME = '/Users/alidsoncaires/Downloads'
    }
    parameters {

        //DURATION
        choice(name: 'DURATION', choices: ['30','60'], description: '')
        //USERS
        string(name: 'USERS', defaultValue: '1', description: '', trim: true)
        //RAMPUP
        choice(name: 'RAMPUP', choices: ['0','30'], description: '')

        

    }
    stages {
        stage('Preparação'){
            steps {
                script {
                    JMETER.nodes = [:]
                    JMETER.nodes.names = '127.0.0.1:1099,192.168.0.6:1099,192.168.0.6:1099,192.168.0.6:8443'
                    JMETER.nodes.active = ''
                    JMETER.nodes.list = []
                    JMETER.projeto = PROJETO
                    JMETER.rotas = ROTAS
                    JMETER.duration = params.DURATION
                    JMETER.users = params.USERS
                    JMETER.rampup = params.RAMPUP
                }
            }
        }
        stage('Validar Cluster') {
            steps {
                script {
                    JMETER.nodes.list = JMETER.nodes.names.tokenize(',').collect{it.tokenize(':').with{[host:it[0],port:it[1],status:'']}}
                    JMETER.nodes.list.findAll{ it.status!="STATUS"}.each{ n ->
                            def EXIT_CODE = sh label:"Node: ${n.host}:${n.port}" , script:"java -jar /Users/alidsoncaires/desenv/java/RMI/target/rmi-status-1.0.jar -h ${n.host} -p ${n.port}", returnStatus:true
                            n.status=(EXIT_CODE==0?'UP':'DOWN')
                    }
                    JMETER.nodes.total = JMETER.nodes.list.size()
                    JMETER.nodes.up = JMETER.nodes.list.findAll{ it.status=="UP"}.size()
                    JMETER.nodes.down = JMETER.nodes.list.findAll{ it.status=="DOWN"}.size()
                    JMETER.nodes.active = JMETER.nodes.list.findAll{ it.status=="UP"}.collect{"$it.host:$it.port"}.join(",")

                    //Formata Validação dos Serviço
                    def lib = load '/Users/alidsoncaires/Downloads/formatTextInColumns.groovy'
                    def dados = JMETER.nodes.list.collect{"$it.host | $it.port | $it.status"}.join(",").replaceAll('\\,','\n')
                    def resumo = lib.formatStrResumo("HOST | PORT | STATUS", dados)
                    resumo = lib.formatTextInColumns(resumo,"|",false)

                    //Formata Resumo
                    dados = "UP | " + JMETER.nodes.up  + "/" + JMETER.nodes.total + " | " + lib.formatPercentual(JMETER.nodes.up, JMETER.nodes.total) + "%\n"
                    dados += "DOWN | " + JMETER.nodes.down  + "/" + JMETER.nodes.total + " | " + lib.formatPercentual(JMETER.nodes.down, JMETER.nodes.total) + "%"
                    dados = lib.formatStrResumo("\nSTATUS | FRAÇÃO | PERCENTUAL",dados)
                    resumo += lib.formatTextInColumns(dados,"|",false)
                    
                    println(resumo)
                }
            }
        }
        stage('Configurar Cluster') {
            steps {
                script {
                    println(JMETER)
                    sh 'printenv'

                }
            }
        }
        stage('Executar Teste'){
            steps{
                script {
                    sh "${env.JMETER_HOME}/bin/jmeter -n -t ${env.PROJETO_HOME}/${JMETER.projeto} -Gduration=${JMETER.duration} -Gusers=${JMETER.users} -Grampup=${JMETER.rampup} -R ${JMETER.nodes.active} -L jmeter.util=DEBUG  -Jjmeter.save.saveservice.output_format=csv  -l  report.jtl"
                }
            }
        }
        stage('Report'){
            steps{
                script {
                    perfReport excludeResponseTime: true, filterRegex: '', ignoreFailedBuilds: true, ignoreUnstableBuilds: true, modePerformancePerTestCase: true, modeThroughput: true, sourceDataFiles: '**/*.jtl'
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/*,*', followSymlinks: false
                }
            }
        }
    }
}
