#!/bin/bash

ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp -r ~/.ssh/ user@slave1:~/
scp -r ~/.ssh/ user@slave2:~/
