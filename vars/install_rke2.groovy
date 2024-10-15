def installRKE2() {
    def command = "ansible-playbook -i ansible/inventory/development ansible/playbooks/install_rke2.yml"
    def process = command.execute()
    process.waitFor()
    if (process.exitValue() == 0) {
        println "RKE2 installation completed successfully."
    } else {
        println "RKE2 installation failed."
        println process.err.text
    }
}

def uninstallRKE2() {
    def command = "ansible-playbook -i ansible/inventory/development ansible/roles/rke2/tasks/uninstall.yml"
    def process = command.execute()
    process.waitFor()
    if (process.exitValue() == 0) {
        println "RKE2 uninstallation completed successfully."
    } else {
        println "RKE2 uninstallation failed."
        println process.err.text
    }
}