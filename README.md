# Onion Routing
_Advanced Internet Computing - Group 4_

## Team members
 * Lisa Fichtinger
 * [Manuel Geier](http://geier.io)
 * Markus Zisser
 * Mihai Lepadat
 * [Thomas Rieder](http://rieder.io)

## Getting started
**Important: These instruction are meant for someone who wants to build the system from the ground up. In case you are grading our solution as part of the course at TU Vienna, please skip ahead to "Evaluation as part of the course" at the bottom of the page.**

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


## Evaluation as part of the course

First off, as specified in the forum you will get a VM from us that only hosts the originator. To get everything running you have to do the following:
 * start the VM
 * logon with credentials: **TODO MIHAI**
 * go the the folder of the orignator: ``cd SOMETHING`` **TODO MIHAI**
 * in the ``conf`` folder there is a file called ``application.conf``. In this file you will need to configure the following:
   * ``directory.http.address`` to the **public IP** of the directory node on EC2
   * ``directory.http.port`` to port 80
   * ``quote.http.address`` to the **public IP** of the quote service on EC2
   * ``quote.http.port`` to port 80
   * the public IPs for EC2 instances are not static
   * this means that you need to start the nodes in EC2 now and get the public IPs as listed in the Amazon web interface
   * once you started the EC2 instances **you need to start the services themself** (see below)
 * **before starting the originator the quote/directory MUST be running**
 * you can then start the originator with the following command: ``bin/originator -Dconfig.file=conf/application.conf``
 * you can access the web interface of the originator at the IP address of the VM on the port configured in ``conf/application.conf`` (the default is ``9000``)

**Note**: There is a bug at the ``/monitor`` resource of the directory node. On one of our team member's PCs the table needs to refreshed to properly show all nodes - we think that the JavaScript library used to render the table is at fault, but were not able to reproduce it on another machine.

### The EC2 Instances

Our EC2 instances are running with the following IDs:
 * Chain nodes: ``G4-T3-Node`` in **Ireland**
 * Directory: ``G4-T3-directory`` in **Frankfurt**
 * Quote: ``G4-T3-quote`` in **Frankfurt**
 * (Originator: ``G4-T3-originator`` in **Frankfurt**)

To connect to the instances please use the private key provided by us (``aic-ec2.ppk``) and the username ``ubuntu``.

To start the originator:
```
sudo -i
cd /root/originator
bin/originator -Dconfig.file=conf/application.conf
```
The originator is now available at port ``80`` (there is an nginx reverse proxy running from ``80`` <-> ``9000``).

To start the quote:
```
sudo -i
cd /root/quote
bin/quote -Dconfig.file=conf/application.conf
```
The quote service is now available at port 80.

To start the directory:
```
sudo -i
cd /root/directory
bin/directory -Dconfig.file=conf/application.conf
```
The directory is now available at port 80.


## Used technologies
 * Java
 * Play Framework
 * REST APIs
 * Ansible
