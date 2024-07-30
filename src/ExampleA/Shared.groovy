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

    def updatePOMVersion(){
         def pomXml = new XmlSlurper().parse(pom.xml)
         pomXml.version[0].value = env.BUILD_NUMBER
         def updatedPomXml = XmlUtil.serialize(pomXml)
         pomFile.write(updatedPomXml)
         echo "Updated POM version to ${env.BUILD_NUMBER}"
    }


    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
     stage('Update Version'){
      updatePOMVersion()
     }
    
  stage('Build'){
      startBuild()
    }
}

    }

return this
