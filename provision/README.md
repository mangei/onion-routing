# Ansible Provisioning

The provision.yml playbook can be used to instantiate new chain nodes as well as ensure that existing chain nodes are running.

It ensures the following:
 * there are #N chain nodes running at EC2 (identified by name)
   * this means that is also _stops_ instances
 * nginx is configured
 * the chain node process is running
 * the processs uses the right config file
