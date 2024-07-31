package ExampleA

    def defaultCheckout() {
        checkout(scm)
    }
def updatePomVersion(String buildNumber) {
    def pomFileContent = readFile 'pom.xml'
    def pomXml = new XmlSlurper().parseText(pomFileContent)
    def versionNode = pomXml.version[0]
    versionNode.value = "1.0.${buildNumber}"
    def updatedXml = XmlUtil.serialize(pomXml)
    writeFile file: 'pom.xml', text: updatedXml
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
