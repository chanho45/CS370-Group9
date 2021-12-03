# CS370-Group9

## Group Members

ChanHo Lee: 45chanho@gmail.com

## Documentation

classes, functionality, and descriptions on here



# Classes

## Bus Class

Bus Icon which will be displayed on the map

> public class Bus extends [com.microsoft.maps.MapIcon](https://docs.microsoft.com/ko-kr/bingmaps/sdk-native/map-control-api/mapicon-class)

### Constructor

> Bus(BusRoute route, Geopoint location, Context context)

### Properties

#### Route

Bus Route of the given Bus Icon

> BusRoute getRoute()



## BusRoute Class

Represents the informations of a bus route

> public class BusRoute

### Constructor

> BusRoute(String id)

### Properties

#### id

id of the bus route

> String getId()

#### color

color of the bus route

> void setColor(int color)
> int getColor()

#### name

the name of the bus route

> void setName(String name)
> String getName()

#### routeNumber

route number of the bus route

> void setRouteNumber(String routeNumber)
> String getRouteNumber()

#### description

simple description of the bus route

> void setDescription(String description)
> String getDescription()



## BusStop Class

### Constructor

> BusStop(String name, String id, BusRoute[] routes, Geopoint location, Context context)

### Properties

#### id

code that identifies the bus stop

> String getId()

#### routes

bus routes that stop in this bus stop

> BusRoute[] getRoutes()



## Map Class

### Constructor

> Map(Context context)

### Properties

#### map

[com.microsoft.maps.MapView](https://docs.microsoft.com/ko-kr/bingmaps/sdk-native/map-control-api/mapview-class) contains the functionality within the Map

> MapView getMapView()

#### pinLayer

[com.microsoft.maps.MapElementLayer](https://docs.microsoft.com/ko-kr/bingmaps/sdk-native/map-control-api/mapelementlayer-class) Layer that gathers the MapIcons displayed in the map

adding push pin to the layer
> void addPin(MapIcon pin)
> void addPin(MapIcon[] pins)

clear the push pins in the layer
> void clearPin()

#### polylineLayer

[com.microsoft.maps.MapElementLayer](https://docs.microsoft.com/ko-kr/bingmaps/sdk-native/map-control-api/mapelementlayer-class) Layer that gathers the MapPolylines displayed in the map

adding polyline to the layer
> void addLine(MapPolyline line)
> void addLine(MapPolyline[] lines)

clear the polylines in the layer
> void clearLine()

## BusMap Class extends Map

## StopMap Class extends Map

## TripMap Class extends Map



# Request Handler

## BusRouteSearch



## LocalSearch

## BusRouteSearch

## BusRouteInfo

## BusStopNear

## BusPathRequest

## SIRI_BusRequest

## SIRI_StopRequest