package ExampleA

void Shared(){
        
def defaultCheckout() {
return checkout(scm)
}
 
def startBuild (String imageName = "maven:3.9.8-amazoncorretto-11") {
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
      def shared = new Shared()
      shared.defaultCheckout()
    }

    
  stage('Build'){
     def shared = new Shared()
      shared.startBuild()
      
    }
}
}
}
return this 




