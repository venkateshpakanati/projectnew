@Library('jenkins-shared') _

def label = "worker-${UUID.randomUUID().toString()}"

//Jenkins workspace
def workingdir = "/home/jenkins"

images = [maven:"maven:3.6.0-jdk-8-alpine", mavenMemLmt:"2Gi", mavenCpuLmt:"1500m"]
def dockerImage = [docker:"trion/jenkins-docker-client", dockerMemLmt:"6000Mi", dockerCpuLmt:"3000m"]
def helmImg = [helm:"venkateshpakanati/helm_push:1.3"]


images << dockerImage 
images << helmImg
 
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
                    // stash name: "code-stash", includes: "**/*"
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
                  // unstash "code-stash"
                  sh 'ls -lrt && mvn -version'
                  sh "mvn -V -B -U -T 8 clean install -s /home/jenkins/.m2/settings.xml"
                  }
                }
              }

              if(isPublishArtifacts) {
                stage('Build helm chart and publish helm chart') {
                  milestone()
                  container('helm') {
                      def chartPath = "projectchart"
                      def releasename ="projectchart";
                      def helmvirtualrepo = "local";
                      sh '''
                        cat /home/groot/helm/repository/repositories.yaml
                        helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                        helm repo update --debug
                        helm repo list --debug
                      '''
                      sh "yq w -i projectchart/Chart.yaml version ${env.BUILD_ID}"
                      sh "yq w -i projectchart/Chart.yaml appVersion ${env.BUILD_ID}"
                      sh "yq w -i projectchart/values.yaml image.tag ${env.BUILD_ID}"
                                  
                      sh '''                                
                        chart_name="projectchart"
                        version=$(helm inspect "$chart_name" | yq r - 'version')
                        helm package projectchart
                        ls -lrt
                        chart_filename="${chart_name}-${version}.tgz"
                        curl -u admin:AP9YMHJpDaRrnUzzyY7e452G742 -X PUT -vvv -T "${chart_filename}" "http://172.42.42.104:8081/artifactory/helm/${chart_filename}"
                      '''
                  }
                }
            
                stage('Build docker image and publish image') {
                  milestone ()
                  container('docker') {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                      def customImage = docker.build("venkateshpakanati/cache-demo:${env.BUILD_ID}")
                      customImage.push()
                    }
                  }
                }
              }  

              if(isDeploy) {
                stage('Run helm') {
                  milestone()
                  container('helm') {
                    sh '''
                      helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                      helm repo update --debug
                  '''	  
                    sh "helm upgrade projectchart http://172.42.42.104:8081/artifactory/helm/projectchart-${env.BUILD_ID}.tgz  --username admin --password AP9YMHJpDaRrnUzzyY7e452G742 --install --force --debug"
                    
                  }
                }
              }           
            }
        }
    }
  } catch(e) {
      // println ${e}
       throw e
  }