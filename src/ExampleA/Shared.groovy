package ExampleA

    import groovy.xml.XmlUtil
    def defaultCheckout() {
        checkout(scm)
    }

    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }

    def updatePomVersion(String buildNumber) {
    def pomFilePath = 'pom.xml'
    if (!fileExists(pomFilePath)) {
        error "POM file not found: ${pomFilePath}"
    }
    def pomXml = readFile(pomFilePath)
    def parsedXml = new XmlSlurper().parseText(pomXml)
    def versionNode = parsedXml.'**'.find { it.name() == 'version' }
    if (versionNode) {
        versionNode.value = env.BUILD_NUMBER
    } else {
        error "No <version> element found in the POM file"
    }
    def updatedPomXml = XmlUtil.serialize(parsedXml)
    writeFile(file: pomFilePath, text: updatedPomXml)

    echo "Updated POM version to ${buildNumber}"
}

    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
     stage('Update Version'){
      updatePomVersion()
     }
    
  stage('Build'){
      startBuild()
    }
}

    }

return this
