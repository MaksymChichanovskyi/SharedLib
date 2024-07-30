package ExampleA
def agentName = 'linux && docker'
def someText = 'Hello!'


void mavenApp(){
        

        
def defaultCheckout() {
return checkout(scm)
}





        
def startBuild (String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
    }
}

}
return this 


/*node(agentName) { //run this part on an agent with label 'linux'
    stage('Checkout') {
       
        checkout scm
    }

    
  stage('Build'){
     def shared = new Shared()
      shared.startBuild()
      
    }
}
*/
