package ExampleA
import groovy.xml.XmlUtil


    def defaultCheckout() {
        checkout(scm)
    }



def updatePomVersion(String buildNumber) {
    def pomFile = readFile('pom.xml')
    def parser = new XmlParser()
    def pomXml = parser.parseText(pomFile)
    def versionNode = pomXml.version[0]
    versionNode.value = "1.0.${buildNumber}"
    def updatedPomFile = XmlUtil.serialize(pomXml)
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
