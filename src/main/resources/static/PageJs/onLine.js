$(function(){
    var bodyH = $("#content").height();
    var screenH = $(window).height();
    if (screenH > bodyH) {
        $("#content").css("height", screenH);
    }
    else {
        $("#content").css("height",bodyH);
    }
    ajaxAll();
    //商家ID列表获取 开始
    $.ajax({
        type:"post",
        dataType:"json",
        url:"/payment/online/allPointDetail",
        success:function(data){
            var bussinessIdValue=data.result.allPoint;
            shopId=bussinessIdValue[0].shopId;
            // console.log(shopId);
            for(var i=0;i<bussinessIdValue.length;i++){
                // if(bussinessIdValue[i].lng!=0||bussinessIdValue[i].lat!=0){
                    $(".select").append('<option value='+bussinessIdValue[i].shopId+'>'+bussinessIdValue[i].shopName+'</option>');
                // }
            }
        },
        error:function(){
            console.log("请求失败");
        }
    });
    //商家ID获取 结束
    //更改商家ID 开始
        $(".select").change(function(){
            shopId=$('.select option:selected').val();
            shopText=$('.select option:selected').text();
            //ajaxFun(shopId,shopText);
            ajaxSingle(shopId);
            if(shopText=="全部"){
                $("#GDmap").empty();
                ajaxAll();
            }
        });
    //更改商家ID 结束

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

});

//画所有点 开始
function pointXY(point){
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
                    $(".FlowData").text(point2[i].OnlineSum);
                    $("#areaPeopleSum").text(point2[i].AreaSum);
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
function ajaxAll(){
    $.ajax({
        type:"post",
        dataType:"json",
        contentType:'application/json',
        url:"/payment/online/allPointDetail",
        // data:JSON.stringify(data),
        success:function(data){
            if(data.code==1){
                var points=data.result.allPoint;
                pointXY(points);
                console.log(points);
            }else{
                console.log(data.msg);
            }
        },
        error:function(){
            alert("请求失败")
        }
    });
}
//画所有点 结束


//选中区域画点 开始
function ajaxSingle(pointId){
    $.ajax({
        type:"post",
        dataType:"json",
        contentType:'application/json',
        url:"/payment/online/allPointDetail",
        // data:JSON.stringify(data),
        success:function(data){
            if(data.code==1){
                var points=data.result.allPoint;
                pointXY(points);
                for(var i=0;i<points.length;i++){
                    if(points[i].shopId==pointId){
                        $(".FlowData").text(points[i].OnlineSum);
                        $("#areaPeopleSum").text(points[i].AreaSum);
                        $(".shop_name").text(points[i].shopName);
                        var intLng=Math.round(points[i].lng);
                        var intLat=Math.round(points[i].lat);
                        var lng=points[i].lng;
                        var lat=points[i].lat;
                        singlePoint(pointId,intLng,intLat,lng,lat);
                    }

                }
                // console.log(point);
            }else{
                console.log(data.msg);
            }
        },
        error:function(){
            alert("请求失败")
        }
    });
}
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
//选中区域画点 结束