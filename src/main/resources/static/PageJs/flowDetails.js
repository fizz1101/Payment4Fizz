$(function(){
    var bodyH = $("#content").height();
    var screenH = $(window).height();
    if (screenH > bodyH) {
        $("#content").css("height", screenH);
    }
    else {
        $("#content").css("height",bodyH);
    }


    ajaxCon();
    function ajaxCon(){
        $.ajax({
            type:"post",
            dataType:"json",
            contentType: "application/json; charset=utf-8",
            url:"/payment/stat/indexData",
            success:function(data){

                if(data.code==1){
                    $("#data").text(data.result.Date);
                    $("#week").text(data.result.Week);
                }else{
                    console.log(data.msg);
                }
                console.log(data);
                // console.log(JSON.stringify(data));
            },
            error:function() {
                alert("请求失败");
            }

        });
    }

    //获取今天的时间开始
    function getTime()
    {
        var data=new Date();
        var year = data.getFullYear();
        var month = data.getMonth()+1;
        var day=data.getDate();
        var monthString,dayString;
        if(month<10){
            monthString="0"+month;
        }else{
            monthString=String(month);
        }
        if(day<10){
            dayString="0"+day;
        }else{
            dayString=String(day);
        }
        var time=year+"-"+monthString+"-"+dayString;
        return time;
    }
    var timeDefault=getTime();
    var timeA=new Date(timeDefault.replace(/-/,"/"));
    var timeB=new Date(timeDefault.replace(/-/,"/"));
//获取今天的时间 结束
//时间更改 开始
    $('#datepick').datepick({
        rangeSeparator:' To ',
        rangeSelect:1,
        monthsToShow: true,
        defaultDate:'+0d',
        onClose:function(){
            var argsMap = {beginTime:'',endTime:''};
            getOperateDate(argsMap);
        }
    });
    function getOperateDate(argsMap)
    {
        if($("#datepick").val() == '')
        {
            argsMap.beginTime = '';
            argsMap.endTime = '';
        }
        else
        {
            var timeArray = $("#datepick").val().split('To');
            argsMap.beginTime =timeArray[0].trim();
            argsMap.endTime = timeArray[1].trim();
            timeA=new Date(argsMap.beginTime);
            timeB=new Date(argsMap.endTime);
            $("#GDmap").empty();
            ajaxMR(shopId,timeA,timeB);
        }
        return argsMap;
    }
//时间更改 结束

//商家ID列表获取 开始
    $.ajax({
        type:"post",
        dataType:"json",
        contentType:'application/json',
        url:"/payment/business/getAllBusiness",
        success:function(data){
            var bussinessIdValue=data.result.business;
            shopId=bussinessIdValue[0].id;
            for(var i=0;i<bussinessIdValue.length;i++){
                $(".select").append('<option value='+bussinessIdValue[i].id+'>'+bussinessIdValue[i].name+'</option>');
            };
            ajaxMR(shopId,timeA,timeB);
        },
        error:function(){
            alert("请求失败");
        }
    });
    //更改商家ID
    $(".select").change(function(){
        shopId=$('.select option:selected').val();
        shopText=$('.select option:selected').text();
        ajaxMR(shopId,timeA,timeB);
    });

});




function ajaxMR(shopNo,startTime,endTime){
    var date = {
        'businessUserId':shopNo,
        'startDate':startTime.getTime(),
        'endDate':endTime.getTime(),
    };
    $.ajax({
        type:"post",
        dataType:"json",
        contentType:'application/json',
        data:JSON.stringify(date),
        url:"/payment/authBusDay/allPointDetail",
        success:function(data){
            if(data.code==1){
                var bussinessIdValue=data.result.allPoint;
                console.log(bussinessIdValue);
                if(shopNo==0){
                    $("#GDmap").empty();
                    pointAll(data.result.allPoint);
                }else{
                    $("#GDmap").empty();
                    for(var i=0;i<bussinessIdValue.length;i++){
                        if(bussinessIdValue[i].shopId==shopNo){
                            $(".FlowData").text(bussinessIdValue[i].FlowSum);
                            $("#areaPeopleSum").text(bussinessIdValue[i].CertPerSum);
                            $(".shop_name").text(bussinessIdValue[i].shopName);
                            var intLng=Math.round(bussinessIdValue[i].lng);
                            var intLat=Math.round(bussinessIdValue[i].lat);
                            var lng=bussinessIdValue[i].lng;
                            var lat=bussinessIdValue[i].lat;
                        }
                    }
                    singlePoint(shopNo,intLng,intLat,lng,lat)
                }
            }else{
                console.log(data.msg);
            }
        },
        error:function(){

        }
    });
}

//	在SVG中加入所有点
function pointAll(point){
    var ewifiGis = new EwifiGis();
    ewifiGis.init("GDmap");
    var pointData=[];
    var pointIds=[];
    for (var i=0; i<point.length;i++){
        if(point[i].lng!=0||point[i].lat!=0){
            var pointDot={
                "id":point[i].shopId,
                "lng": point[i].lng,//value1
                "lat": point[i].lat,//value2
                "style":{
                    "pointPath" : "#xjwifi",
                    "width" : "30",
                    "height" : "30",
                    "opacity" : "",
                    "fill" : "red"
                }
            };
            pointIds.push(point[i].shopId);
            pointData.push(pointDot);
            console.log(pointDot);
        }
    };
    //点的鼠标事件
    function onLoad(point,point2){
        var point1 = document.getElementById(point);
        //		鼠标移上去的效果
        point1.onmousemove = showTip;
        function showTip(){
            for(var i=0; i<point2.length;i++){
                if(point2[i].shopId==point){
                    $(".shop_name").text(point2[i].shopName);
                    $(".FlowData").text(point2[i].FlowSum);
                    $("#areaPeopleSum").text(point2[i].CertPerSum);
                }
            }
            var MapResult = document.getElementById('MapResult'); //将要弹出的层
            MapResult.style.display="block"; //MapResult初始状态是不可见的，设置可为可见
            //window.event代表事件状态，如事件发生的元素，键盘状态，鼠标位置和鼠标按钮状.
            //clientX是鼠标指针位置相对于窗口客户区域的 x 坐标，其中客户区域不包括窗口自身的控件和滚动条。
            MapResult.style.left= event.clientX-25+'px'; //鼠标目前在X轴上的位置，加10是为了向右边移动10个px方便看到内容
            MapResult.style.top= event.clientY-120+"px";
            MapResult.style.position="absolute"; //必须指定这个属性，否则div3层无法跟着鼠标动
            // var MapResult =document.getElementById('MapResult');
        };
        //		鼠标移下来的效果
        point1.onmouseout = closeTip;
        function closeTip(){
            var MapResult = document.getElementById('MapResult');
            MapResult.style.display="none";
        }
    }
    option =
        {
            "MAP": {
                "mapName": "EwifiMap",    //地图名称
                "mapType": "Custom",      //地图类型
                "isDrag": "true",         //是否拖拽
                "isZoom": "true",
                "isCentered": "true",
                "centosLng":2340,
                "centosLat":1097,
            },
            "point" :pointData,
        };
    var ewifiGis = new EwifiGis();
    ewifiGis.init("GDmap");
    ewifiGis.setOption(option);
    ewifiGis.execute();
    for(var i=0; i < pointIds.length;i++){
        onLoad(pointIds[i],point);
    }
}

//	在SVG中加入单个点
function singlePoint(pointId,intLng,intLat,lng,lat){
    $("#GDmap").empty();
    var ewifiGis = new EwifiGis();

    ewifiGis.init("GDmap");
    //点的鼠标事件
    function onLoad(point){
        var point1 = document.getElementById(point);
        //		鼠标移上去的效果
        point1.onmousemove = showTip;
        function showTip(){
            var MapResult = document.getElementById('MapResult'); //将要弹出的层
            MapResult.style.display="block"; //MapResult初始状态是不可见的，设置可为可见
            //window.event代表事件状态，如事件发生的元素，键盘状态，鼠标位置和鼠标按钮状.
            //clientX是鼠标指针位置相对于窗口客户区域的 x 坐标，其中客户区域不包括窗口自身的控件和滚动条。
            MapResult.style.left= event.clientX-25+'px'; //鼠标目前在X轴上的位置，加10是为了向右边移动10个px方便看到内容
            MapResult.style.top= event.clientY-120+"px";
            MapResult.style.position="absolute"; //必须指定这个属性，否则div3层无法跟着鼠标动
            // var MapResult =document.getElementById('MapResult');
        };
        //		鼠标移下来的效果
        point1.onmouseout = closeTip;
        function closeTip(){
            var MapResult = document.getElementById('MapResult');
            MapResult.style.display="none";
        }
    }
    option =
        {
            "MAP": {
                "mapName": "EwifiMap",    //地图名称
                "mapType": "Custom",      //地图类型
                "isDrag": "true",         //是否拖拽
                "isZoom": "true",
                "isCentered": "true",
                "centosLng":intLng,
                "centosLat":intLat,
            },
            "point" :[{
                "id":pointId,
                "lng":lng,
                "lat":lat,
                "style":{
                    "pointPath" : "#xjwifi",
                    "width" : "50",
                    "height" : "50",
                    "opacity" : "",
                    "fill" : "red"
                }
            }],
        };
    var ewifiGis = new EwifiGis();
    ewifiGis.init("GDmap");
    ewifiGis.setOption(option);
    ewifiGis.execute();
    onLoad(pointId);

}


