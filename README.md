# SemanticDBPediaReader

***DRAFT***

Collection of example projects of how to use the SemanticMachineReading ML-framework.

This project contains 5 semi-automatic extracted data sets by aligning wikipedia full text articles with dbpedia infoboxes. 
Each data set contains a number of distantly supervised documents for relation extraction / slot filling. (For mor details see below)

All data sets are designed slot-filling tasks in which a given information extraction template containing a number of attributes need to be filled with the correct entities from the text. 
Each domain is defined by its own specification files defining the template structure (slots / slot-filler) and the scope of the search space in general.  
The specification files can be found in: */src/main/resources/dataset/specifications/csv/*
There are four files: 
1.  entities.csv  contains a list of entities flagged as true iff the entity is of type literal e.g. the birth year of a person. E.g. "White_House false"
2.  hierarchies.csv  contains a list of *entity - super-entity* pairs. This file defines the hierarchical structure of entities in this scope. E.g. "Structure  White_House"
3.  slots.csv contains a list of slots and their maxmimum number of slot-filler. A maximum of < 0 means no limitation. E.g. "windows  -1" or "house_number  1"
4.  structures.csv  contains a list of *slot - slot-filler* pairs per entity. Possible slot-filler values depends on the hierarchy file. All sub-entities of the specified one can be serve as slot-filler. 


The five data sets are: 

SoccerPlayer 
Dam
Film
Single
ArchitecturalStructure 

