/**
 * Created by Tomer on 25/04/2015.
 */
 
/*
 * Private function, gets a list of objects and a list of fields (strings),
 * retreive a list of minimized objects including the required fields only.
 */
function extract(objectList, fieldList) {
    var minimizedList = [];
    var tempObj = {};
    for (obj in objectList) {
        tempObj = {};
        for (field in fieldList) {
            var tmp = fieldList[field];
            tempObj[tmp] = objectList[obj][tmp];
        }
        minimizedList.push(tempObj);
    }
    return minimizedList;
}
 
/*
 * Private function, gets a list of objects and a name of a field (string),
 * retrieves a partial list with no repetition in this field.
 * objects do not have to be of the same type.
 * if object does not have the field - field's value is undefined.
 */
 
//function distinct (objectList,field) {
  //  var stringArray = [];
   // for (var i=0; i<objectList.length; i++){
     //   var currentRow = objectList[i].get(field);
   // }
    /*
    var result = [];
    var tempDict = {};
    for (obj in objectList) {
        var tmp = objectList[obj][field];
        if (tempDict[tmp] === undefined) {
            tempDict[tmp] = 1;
            result.push(objectList[obj]);
        }
    }
    return result;
    */
 
//}