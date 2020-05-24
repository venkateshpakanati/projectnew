@Library('jenkins-shared') _

def label = "worker-${UUID.randomUUID().toString()}"

//Jenkins workspace
def workingdir = "/home/jenkins"
def images = ""
def mavenImage = [maven:"maven:3.6.0-jdk-8-alpine", mavenMemLmt:"2Gi", mavenCpuLmt:"1500m"]
def dockerImage = [docker:"trion/jenkins-docker-client", dockerMemLmt:"6000Mi", dockerCpuLmt:"3000m"]
def helmImg = [helm:"venkateshpakanati/helm_push:1.3"]

images << mavenImage
 
try {
    timestamps {
       slaveTemplate = new PodTemplate(label, images, workingdir, this)
       slaveTemplate.BuilderTemplate {
           node(slaveTemplate.podlabel) {
                def app
                def props = null
                boolean isBuildApp = false
                boolean isPublishArtifacts = false
                boolean isDeploy = false
                stage('Checkout Code') {
                  milestone ()
                  sh """
                        git config --global http.proxy ''
                        git config --global https.proxy ''
                    """
                    // git branch: 'master',
                    // credentialsId: '35205444-4645-4167-b50e-c65137059f09',
                    // url: 'http://13.234.176.102/venkateshpakanati/mymicroservices.git'
                    def myRepo = checkout scm
                    def gitCommit = myRepo.GIT_COMMIT
                    def gitLocalBranch = myRepo.GIT_LOCAL_BRANCH 
                    def gitPrevCommit = myRepo.GIT_PREVIOUS_COMMIT
                    def gitPrevSuccessCommit = myRepo.GIT_PREVIOUS_SUCCESSFUL_COMMIT
                    def gitBranch = myRepo.GIT_BRANCH
                    def shortGitCommit = "${gitCommit[0..10]}"
                    def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)
                    println "${gitCommit}   ${gitBranch}  ${shortGitCommit}  ${previousGitCommit}   ${gitLocalBranch}  ${gitPrevCommit} ${gitPrevSuccessCommit}"
                  
                    // sh "ls -lat"
                    //stash name: "code-stash", includes: "**/*"
                }
                
                stage('load properties') {
                  milestone()
                  if (env.BRANCH_NAME == 'develop') {
                    props = readProperties file:'jenkins-develop.properties'
                  } else if (env.BRANCH_NAME =~ /^(release|hotfix)/) {
                    props = readProperties file:'jenkins/jenkins-release.properties'
                  } else {
                    props = readProperties file:'jenkins-default.properties'
                  }  
                
                  isBuildApp = props.build.toBoolean()
                  isPublishArtifacts = props.publishartifacts.toBoolean()
                  isDeploy = props.deploy.toBoolean()

                }

              if(isBuildApp) {
                stage('Build maven project') {
                  milestone ()
                  container('maven') {
                 //  unstash "code-stash"
                   sh 'ls -lrt && mvn -version'
                   sh "mvn -V -B -U -T 8 clean install -s /home/jenkins/.m2/settings.xml"
                   stash name: "code-stash", includes: "**/*"
                  }
                }
              }
           }
         }   

      //   images = images - mavenImage
      //   images << helmImg 

      //    slaveTemplate = new PodTemplate(label, images, workingdir, this)
      //    slaveTemplate.BuilderTemplate {
      //      node(slaveTemplate.podlabel) { 
      //         if(isPublishArtifacts) {
      //           stage('Build helm chart and publish helm chart') {
      //             milestone()
      //             container('helm') {
      //                 def chartPath = "projectchart"
      //                 def releasename ="projectchart";
      //                 def helmvirtualrepo = "local";
      //                 helmutil = new helmUtility();
      //                 helmutil.buildAndPublishChart(chartPath,releasename,helmvirtualrepo);
      //             }
      //           }
      //         }
      //      }  

      //   images = images - helmImg
      //   images << dockerImage 
       
      //    slaveTemplate = new PodTemplate(label, images, workingdir, this)
      //    slaveTemplate.BuilderTemplate {
      //      node(slaveTemplate.podlabel) {    
      //           stage('Build docker image and publish image') {
      //             milestone ()
      //             container('docker') {
      //               unstash "code-stash"
      //               docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
      //                 def customImage = docker.build("venkateshpakanati/cache-demo:${env.BUILD_ID}")
      //                 customImage.push()
      //               }
      //             }
      //           }
      //      }
      //   }             

      //   images = images - dockerImage
      //   images << helmImg

      //  slaveTemplate = new PodTemplate(label, images, workingdir, this)
      //  slaveTemplate.BuilderTemplate {
      //      node(slaveTemplate.podlabel) {
      //         if(isDeploy) {
      //           stage('Run helm') {
      //             milestone()
      //             container('helm') {
      //               helmutil = new helmUtility();
      //               helmutil.deploy()
      //             }
      //           }
      //         }           
      //       }
      //   }
      // }
    } 
  } catch(e) {
      // println ${e}
       throw e
  }