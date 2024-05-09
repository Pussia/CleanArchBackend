# Clean Architecture Backend Service
## Introduction
Clean Architecture Backend service is the service that gives information about **python** project's architecture.

### What can this service do?
This service can give information about violators of ADP (acyclic dependency principle), multiple inheritance.
Also gives information about global variables in the project and gives detailed information about each component in the project, like fanIn, fanOut etc.


## How to install
1. ```git clone https://github.com/Pussia/CleanArchBackend.git```
2. ```gradle clean build```
3. ```docker build -t clnarch .```
4. ```docker run --rm -d -p 8080:8080 clnarch```

## How to use
### Request
Send request to http://localhost:8080/api/v1/projects with the following query parameters:

1. link - link to the git repo. (For ex. https://github.com/Pussia/asciiTree.git).
2. branch (optional) - the branch you want to clone. (For ex. main).

```curl -X POST http://localhost:8080/api/v1/projects?link=https://github.com/Pussia/asciiTree.git&branch=main```

### Response
Response JSON have 4 fields:
* adpFiles - list of files that violate acyclic dependency principle (ADP). Cyclic dependencies union into the list.
* globals - all global variables in the project.
* componentsInformation - an information about component:
  * fanIn - number of components that depends on current one
  * fanOut - number of components that current one depends on
  * numberOfClasses - number of classes in the component
  * numberOfConcreteClasses - number of non-abstract classes in the component
  * numberOfAbstractClasses - number of abstract classes in the component
  * abstractness - abstractness of the current component (numberOfAbstractClasses / numberOfClasses)
  * instability - instability of the current component (fanOut / (fanIn + fanOut))
  * distance - distance from the main sequence
  * component - relative path to the current component
* multipleInheritance - list of files that violate multiple inheritance principle.
```json
{
    "adpFiles": [],
    "globals": [],
    "componentsInformation": [],
    "multipleInheritance": []
}
```

### Example
Request:

$ ```curl -X POST http://localhost:8080/api/v1/projects?link=https://github.com/Pussia/asciiTree.git&branch=main```

Response:

```json
{
    "adpFiles": [],
    "globals": [
        {
            "name": "filenames",
            "filePath": "/main.py",
            "pointer": {
                "variablePointer": 37,
                "global": true
            },
            "global": true
        }
    ],
    "componentsInformation": [
        {
            "fanIn": 0,
            "fanOut": 1,
            "numberOfClasses": 0,
            "numberOfConcreteClasses": 0,
            "numberOfAbstractClasses": 0,
            "abstractness": 0.0,
            "instability": 1.0,
            "distance": 0.0,
            "component": "/main.py"
        }
    ],
    "multipleInheritance": []
}
```

