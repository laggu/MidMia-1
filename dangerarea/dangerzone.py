from mia_location import MiaLocation
import json
from collections import OrderedDict

f = open("mia_location.txt","r")
addresses = f.readlines()
f.close()

addresses.pop(0)
print(addresses)

list = []

for address in addresses:
    temp = MiaLocation(address)
    list.append(temp)

print(list)

for i in range(0,len(list)):
    for j in range(0, len(list)):
        dis = list[i].caldistance(list[j])
        if dis < 2:
            list[i].nearLocations.append(list[j])
    list[i].numberOfNearLocation = len(list[i].nearLocations)
    print(i)

print('-'*100)

dangerzonelist = []

for i in range(0,len(list)):
    if list[i].isbiggest():
       dangerzonelist.append(list[i])

for i in range(0,len(dangerzonelist)):
    dangerzonelist[i].calradius()
    temp = i
    for j in range(i, len(dangerzonelist)):
        if(dangerzonelist[temp].numberOfNearLocation<dangerzonelist[j].numberOfNearLocation):
            temp = j
    dangerzonelist[i],dangerzonelist[temp] = dangerzonelist[temp], dangerzonelist[i]

print(dangerzonelist)
print(len(dangerzonelist))
print('*'*100)

list = dangerzonelist[0:1]
print(list[0])
print(list)
print(len(list))
print('*'*100)

for i in range(1,len(dangerzonelist)):
    flag = True
    for point in list:
        point.calradius()
        distance = point.caldistance(dangerzonelist[i])
        print(distance, point.radius)
        if distance * 1000 < point.radius:
            flag = False
            break
    if flag == True:
        list.append(dangerzonelist[i])

for i in range(0,len(list)):
    print(list[i].radius, end=" : ")
    for j in range(0, len(list)):
        print(list[i].caldistance(list[j]), end=' ')
    print()

print(list)
print(len(list))

dangerzonelist = list[:]

group_data = OrderedDict()
for i in range(0,len(dangerzonelist)):
    group_data[i] = dangerzonelist[i].__str__()

json.dumps(group_data, ensure_ascii=False, indent="\t")
#print(json.dumps(group_data, ensure_ascii=False, indent="\t"))

with open('dangerarea.json', 'w', encoding="utf-8") as make_file:
    json.dump(group_data, make_file, ensure_ascii=False, indent="\t")