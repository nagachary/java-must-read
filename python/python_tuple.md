### Tuple

1.  Lists are mutable but tuples are immutable
2. Tuples are created with angular braces( element1, element 2) or element1, element2,
3. For single element tuple will be declared as  tuple = 1,
4. Without , (comma) at the end the single element tuple will be treated as a integer declaration above. When we have more than 1 value in tuple the last comma (,) is optional, also the angular braces.
5. `t = 1, 2, 3,` and t = (1, 2, 3) both are same
6. We can use slicing operator to assign or modify values in tuple as it is immutable
7. We can use slicing operator to read values
8. Empty tuple created as : empty_tuple = ()
9. The print function will always show braces. ()
10. t = (1, 2, 3, )
    1. t1 = t will create a new tuple - t1
    2. del t1 - will delete tuple t1
    3. del t1 will not delete original tuple t
    4. Slicing also can create a new tuple : a1 = t[:]
    5. t = (4, 5, 6, ) => this will override the original tuple t with new values (4, 5, 6, ).
    6. t[1] will read the 1st index element.
    7. Two tuples can be added like  t + a1
    8. Tuple can be multiplied with number t * 2 => (1, 2, 3, 1, 2, 3)
    9. t3 = (1, 'a', True, 1.0, 'tuple') : tuple accepts elements of different data types.
11. Lists are used when we have many values of the same data types. They are used when the values represent examples of same class or same phenomenon.
12. Tuples are often used when we group together values of different types that are somehow related together they form some sort of structure or some sort of bigger data.
13. Tuples are used for certain python operations : for example to swap the elements of a list, we use tuples
    1.  l = [1, 2, 3, 4]
    2. l[0], l[1] = l[1], l[0]
    3. Print(l) : [2, 1, 3, 4]
14. Tuple is immutable but if it contains a list which is mutable, then the list in tuple still can be modified.
    1. t = (1, 12.12, True, [1, 2, 3, 4, 5])
    2. count = 1
    3. for i in t:
    ``` 
        if i == t[len(t)-1]:
             for j in range(len(i)):
                 count += 1
                 i[j] = count
         print(t)
     O/p: 1, 12.12, True, [2, 3, 4, 5, 6]) ```