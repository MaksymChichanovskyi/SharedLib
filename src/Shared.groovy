package ExampleA

def installMaven (String imageName){
docker.image(imageName).pull()
docker.image(imageName).inside(){
sh "maven clean package"
      }
    }
return this 
