package ExampleA

void installMaven (String imageName){
docker.image(imageName).pull()
docker.image(imageName).inside(){
sh "maven clean package"
      }
    }
return this 
