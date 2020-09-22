@NonCPS
def formatTextInColumns(def text, def separator, boolean mantainSeparator){
	if (!text){
		return ""
	}
	def columnSize = []
	def result = ""
	text.eachLine{
		it.split("\\"+separator).eachWithIndex{element, index ->
			if (element.length()>columnSize[index]?:0){
				columnSize[index]=element.length()
			}
		}
	}
	text.eachLine{
		it.split("\\"+separator).eachWithIndex{element, index ->
			result += element + ' ' * ((columnSize[index]?:0)-element.length()) + (mantainSeparator?separator:" ")
		}
		result += "\n"
	}
	return result
}

@NonCPS
def formatStrResumo(def header, def dados){
	def br = "\n"
	def formated = ""

	formated +=  header
	formated += br
	formated += dados
	formated += br
	return formated
}

def formatPercentual(def qtd,def total){
	def intQtd = qtd as int
	def intTotal = total as int
	return ((qtd/total)*100) as int
}
return this