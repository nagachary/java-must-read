### Python Dictionary:

1. Dictionaries are `collections used to store key value pair`.
2. `dict1 = {1 : 'one', 2: 'two', 3: 'three'}`
    1. dict1[1] gives value ‘one’
3. dict2 = {(1, 2) : ['one', 'two'], (3, 4) : ['three', 'four']}
    1. dict2[(1, 2)] gives value ['one', 'two']
4. `The key should be an immutable data type.`
5. We cannot use list as keys as list is a mutable data type.
6. `Each key must be unique`. If we give duplicate keys, python uses the latest or recent pair of key value
7. Empty dictionary: empty_dict = {}
8. We can use `.update({key: value})` method to update the dictionary, or we can assign a value to specific key of the dictionary e.g: `dict1[key] = value`
9. We can delete a record using the del operation, like `del dict1[key]`
10. We can use the `.keys()` method to read all the keys of the dictionary.
    1. `dict1.keys()`
11. Or the for loop will give us all the keys
    1. For keys in dict1:
        1. print(keys)
12. To read only values we can use the `.values()` method in dictionary.
    1. `dict1.values()`
13. We can use `.items()` method to read both key and values of the dictionary
    ````
    1. dict1.items()
    2. It returns each key and value as a tuple (key, value)
    3. for key, val in dict1.items():
    4.print(key, '-', val) 
    
Previous: [Python Collections](./python_collections.md)