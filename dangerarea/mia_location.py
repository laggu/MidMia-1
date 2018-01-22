from math import sin, cos, sqrt, atan2, radians, floor

class MiaLocation():
    def __init__(self, s):
        list = s.split(",")
        self.index = list[0]
        self.address = list[1]
        self.longitude = round(float(list[2]),6)
        self.latitude = round(float(list[3]),6)
        self.nearLocations = []
        self.numberOfNearLocation = 0
        self.radius = 0

    def caldistance(self, obj):
        r = 6373.0

        lat1 = radians(self.latitude)
        lat2 = radians(obj.latitude)
        lon1 = radians(self.longitude)
        lon2 = radians(obj.longitude)

        dlon = lon2 - lon1
        dlat = lat2 - lat1

        a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
        c = 2 * atan2(sqrt(a), sqrt(1 - a))

        distance = r * c

        return distance

    def isbiggest(self):
        if self.numberOfNearLocation < 10:
            return False
        list = []

        for location in self.nearLocations:
            list.append(location.numberOfNearLocation)

        list.sort()


        if self.numberOfNearLocation > list[floor(len(list)*0.5)]:
            return True
        else:
            return False


    def calradius(self):
        r = self.numberOfNearLocation//10
        if r >= 15:
            r = 15
        r *= 100
        self.radius = r


    def __str__(self):
        self.calradius()
        return str(self.latitude) + "," + str(self.longitude) + "," + str(self.radius)