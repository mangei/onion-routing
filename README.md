# Onion Routing
_Advanced Internet Computing - Group 4_

## Team members
 * Lisa Fichtinger
 * [Manuel Geier](http://geier.io)
 * Markus Zisser
 * Mihai Lepadat
 * [Thomas Rieder](http://rieder.io)

## Getting started
_The below instructions were tested on Ubuntu LTS_

First of all you need to install ``activator`` and its dependencies. Check the website of the Play Framework for details (https://www.playframework.com/download). After that you can either run or build the appliations.

To **run** them, change into the corresponding directory and execute ``activator run``. This will start the applicalication on the default port 9000. This might be good for taking a look at a single application, but is suboptimal for examining the whole system.

To **build** them, change into the directory and execute ``activator dist`` to generate a binary. Alternatively you may use the ``build.sh`` script. This script will generate binaries for all services and store them in a ``build/`` folder. After building you need to extract the ZIP files and start the applications like this:
```
# name is one of the following: node, originator, quote, directory
bin/$name -Dconfig.file=conf/application.conf
```
To set the HTTP Port the application binds to, edit the ``application.conf`` file. In case the services are running on different machines you will also need to set the IP address and the port at the bottom of the configuration file. Services other than the directory node also require the IP/Port of the other services to be set in the same file.

You should have already installed the Java 7 SDK when installing ``activator``. In case you don't have it installed or you want to run the application on another machine follow the instructions available [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html). 

If you want to keep them running after the shell is closed, do something like:
```
nohup bin/originator -Dconfig.file=conf/application &
``` 

Note: The directory service requires some additional work before it can be started. You need to create a folder for the provisioning logs (in the root of the directory folder) and create a ``provision_nodes`` script. This script can be placed anywhere in the ``PATH``. If you don't want any provisioning just create and empty script.
```
mkdir provision-logs
```
```
vim /usr/bin/provision_nodes

#!/usr/bin/env bash
ansible-playbook /root/onion-routing/provision/provision.yml

chmod +x /usr/bin/provision_nodes
```

## Deployment

For the deployment you will need to install Ansible (http://www.ansible.com/home). For Ubuntu the whole configuration looks like this:
```
apt-get install ansible python-boto
```

Since we will run a provision playbook we will need to specify an inventory file with localhost
```
vim /etc/ansible/hosts
[local]
127.0.0.1
```

We then add the private key to login to the machines to the server:
```
vim /etc/ansible/ansible.cfg
private_key_file = /etc/ansible/private.key
host_key_checking = False

vim /etc/ansible/private.key
# paste key

# protect teh key
chmod 400 /etc/ansible/private.key
```

### Creating new chain nodes
The ``provision.yml`` playbook can be used to instantiate new chain nodes as well as ensure that existing chain nodes are running.

It ensures the following:

 * there are #N chain nodes running at EC2 (identified by name)
   * this means that it also _stops_ instances
 * nginx is configured
 * the chain node process is running
 * the process uses the right config file

**This playbook assumes that there is a ``node-binary.zip`` file in the "directory"-folder. This file should be created by using ``activator dist`` for the chain-node (see above).**

It also assumes that the following environment variables are set:
 
 * ``AWS_SECRET_ACCESS_KEY``
 * ``AWS_ACCESS_KEY_ID``


## Used technologies
 * Java
 * Play Framework
 * REST APIs
 * Ansible
