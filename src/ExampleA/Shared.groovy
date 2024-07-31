package ExampleA
import groovy.xml.XmlUtil


    def defaultCheckout() {
        checkout(scm)
    }

@NonCPS
def updatePomVersion(String buildNumber) {
    def pomFile = readFile('pom.xml')
    def parser = new XmlParser()
    def pomXml = parser.parseText(pomFile)
    pomXml.version[0].value = "1.0.${buildNumber}"
    def updatedPomFile = groovy.xml.XmlUtil.serialize(pomXml)
    writeFile(file: 'pom.xml', text: updatedPomFile)
    echo "Updated pom.xml with build number: ${buildNumber}"
}


    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }

    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
    stage ('Update Pom.xml'){
        updatePomVersion()
    }

  stage('Build'){
      startBuild()
    }
    }
    }

return this
