# RELOC Evaluator

Evaluator for [reloc](https://github.com/camsenec/reloc), which is an edge server assignment system for static sensors.



## Overview

1. Register edge servers
2. Register sensors (clients)
3. A "home server" is assigned to each sensors based on the pub/sub relation among the sensors
4. Resource usage of edge servers are calculated by simulation.
5. Home server is updated so that any edge server's resource is not consumed over limitation.



### Sensor distribution

<div align="center"><img width=700px src="https://reloc.s3.eu-north-1.amazonaws.com/client_distribution.png"></div>



### Home server assignment and resource usage

<div align="center"><img width=700px src="https://reloc.s3.eu-north-1.amazonaws.com/used.png"></div>



<div align="center"><img width=700px src="https://reloc.s3.eu-north-1.amazonaws.com/cp.png"></div>





## Usage

```
git clone https://github.com/camsenec/reloc-evaluator && cd reloc-evaluator
```

### Using Docker

```
docker-compose up
```


### From source

In a console,

```
git clone https://github.com/camsenec/reloc && cd reloc
pip install -r requirements.txt
python manage.py runserver
```



In another console

```
git clone https://github.com/camsenec/reloc-evaluator && cd reloc-evaluator
mkdir app/cache app/log app/result
gradle clean run
```



## Visualize

```
cd grapher
pip3 install -r requirements.txt
python3 grapher.py
```

