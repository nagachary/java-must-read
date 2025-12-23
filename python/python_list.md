### python List:
1. empty_list = []
2. top_cities = [‘city1’, ‘city2’, ‘city3’, ‘city4’, ‘city5’]
3. top_cities[-1] will return the last element of the list
4. top_cities[-5] returns the first element, here first element is the last element from reverse. This will return 5th element from the end.
5. Slicing: (first argument inclusive and second one is exclusive): top_cities[0:2] : will return elements starting from first index mentioned here 0 to last element before the index mentioned after colon(:), second argument is excluded


1. Here top_cities[0] and top_cities[1] are included and top_cities[2] is excluded.
2. Slicing also will work in reverse : top_cities[-3:-1] :
3. top_cities[0:] : returns all elements starting from 0th index till last
4. top_cities[:4] : returns all the elements excluding the list from 4th index
5. top_cities[:]: this returns all the elements
6. Slicing will not return any error with non-existing indices: top_cities[10:20] will return empty ([]) list.
7.  top_cities = ['city1', 'city2', 'city3', 'city4', 'city5', 'city6']


1. `del top_cities[5]`  : will delete `city6` from list and adjust the indexes.
2. `del top_cities[3:]` : deletes all the list elements starting from 3rd index
3. `del top_cities[:]` : will delete all the elements in the list
4. `del top_cities` : will delete the list itself.
5. del is an instruction but not a function call.
6. methods belong to data, without data methods doesn’t’t exist, methods are invoked on data using the dot (.) on data with method name and arguments.
7. functions doesn’t belong to data. functions will not be called/invoked on data
8. E.g: `.append()` js a method on list
9. Methods are functions that belongs to specific data
10. `insert(index, value)`: insert will insert the given value at the given index
11. `len(top_cities)` will return the length or size of the list
12. `len(‘city1’)` will return the length of the string ‘city1’ which is 5
13. `range(len(top_cities))` will return the - range of indexes from o to length-1 index.
14. `range(5)` generates: [0, 1, 2, 3, 4]
15. `range(1, 5)` generates [1, 2, 3, 4]
16. Swapping elements : list = [50, 20, 40, 30, 10]
    1. a=40
    2. b=50
    3. a, b = b, a
    4. Same can be applied to list elements
17. Python has `.sort()` method available in list
    1. list = [50, 20, 40, 30, 10]
    2. list.sort()
    3. print(list)
    4. O/P: [10, 20, 30, 40, 50]
18. For reverse sort use `.sort(reverse=True)`
    1. list = [10, 20, 30, 40, 50]
    2. `list.sort(reverse=True)`
    3. print(list)
    4. O/P: [50, 40, 30, 20, 10]
19. `sorted(list)` is a function available which will result a sorted list and it keeps the original list as is
    1. print(sorted(list))
    2. list : [50, 40, 30, 20, 10]
    3. sorted(list): [10, 20, 30, 40, 50]
    4. `if element in list` : will help us to identify if the specified element is present in list. Behind the scenes both the variables point to the same very place in the memory.
    5. create a list from another list
       1. `list1 = list2`
       2. here list1 and list2 points to the same reference
    6. In complex data types like lists, the name of the list doesn’t point to actual list in the computer memory, instead the name of the list is the name of the memory location where the list is stored. We call these as references
    7. To keep both the lists independently, we can use slicing for copying the list
       1. new_list = original_list[:]
       2. new_list = original_list[:2]
20. List comprehensions:
    1. Use for loop to create a new list
    2. numbers = []
    3. for i in range(1, 101):
    4. numbers.append(i)
    5. print(numbers)
21. Use comprehension:
    1. new_numbers = `[i for i in range(1, 101)]`
    2. print(numbers)
22. Filter in comprehension
    1. `del new_numbers`
    2. e.g: filter numbers divisible by 3
    3. new_numbers = `[i for i in range(1, 101) if i%3 == 0]`
    4. print(numbers)
23. Create two-dimensional list using comprehension:
    1. numbers_latest = `[[i for i in range(4)] for j in range(4)]`
    2. print(numbers_latest)
    3. 0/p: [[0, 1, 2, 3], [0, 1, 2, 3], [0, 1, 2, 3], [0, 1, 2, 3]]
24. Add two lists: using + (plus)
    1. list1 = [1,2,3,4, 5]
    2. list2 = [6, 7, 8]
    3. `result_list = list1 + list2`
    4. O/p: [1, 2, 3, 4, 5, 6, 7, 8]
25. Multiply a list with numbers
    1. `str_list = ['a', 'b', 'c']`
    2. `str_list * 3`
    3. List elements are repeated 3 times, which means same list is added like 3 times
    4. `o/p: ['a', 'b', 'c', 'a', 'b', 'c', 'a', 'b', 'c']`

Previous: [Python Collections](./python_collections.md)