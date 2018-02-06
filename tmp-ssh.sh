project_home=/home/beta/Workspace/enterovirus

cd $project_home
cd immunessh
sudo cp git-authorization.sh /home/git
sudo chmod +x /home/git/git-authorization.sh
mvn clean compile assembly:single
sudo cp target/immunessh-0.0.1-prototype-jar-with-dependencies.jar /home/git
