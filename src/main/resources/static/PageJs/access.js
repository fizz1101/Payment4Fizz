$(document).ready(function(){
    //获取屏幕高度 使没有内容的情况下 背景铺满整个页面
    var bodyH = $("#content").height();
    var screenH = $(window).height();
    if (screenH > bodyH) {
        $("#content").css("height", screenH);
    }
    else {
        $("#content").css("height",bodyH);
    }

    var line1X,line1Y,line2X,line2Y;
    //认证人数按钮 图
    $("#people").click(function(){
        var People = document.getElementById("people");
        var Flow = document.getElementById("flow");
        var flowCss = $(Flow).hasClass("myButton");
        if(flowCss){
            $(Flow).removeClass("myButton");
            $(People).addClass("myButton");
        }else{
            $(People).addClass("myButton");
        };
        accessOption = lineCharts('rgba(0, 222, 255,1)','rgba(0, 222, 255,0.2)',line1X,line1Y,"");
        var access = echarts.init(document.getElementById('access'));
        access.setOption(accessOption);
    });
    //认证流量趋势按钮 图
    $("#flow").click(function(){
        var People = document.getElementById("people");
        var Flow = document.getElementById("flow");
        var peopleCss = $(People).hasClass("myButton");
        if(peopleCss){
            $(People).removeClass("myButton");
            $(Flow).addClass("myButton");
        }else{
            $(Flow).addClass("myButton");
        };
        accessOption = lineCharts('rgba(87, 202, 18,1','rgba(87, 202, 18,0.2)',line2X,line2Y,"");
        var access = echarts.init(document.getElementById('access'));
        access.setOption(accessOption);
    });
    //获取商家ID
    var strUrl=window.location.href;
    var shopIdIndex=strUrl.indexOf("=");
    var shopId=strUrl.substring(shopIdIndex+1,strUrl.length);


    //获取今天的时间
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
    // var timeDate=new Date(timeDefault.replace(/-/,"/"));
    var timeA=new Date(timeDefault.replace(/-/,"/"));
    var timeB=new Date(timeDefault.replace(/-/,"/"));

    function ajaxFun(shopId,startTime,endTime){
        var data = {
            'businessUserId':shopId,
            'startDate':startTime.getTime(),
            'endDate':endTime.getTime()
        };
        $.ajax({
            type:"post",
            dataType:"json",
            contentType:'application/json',
            data:JSON.stringify(data),
            url:"/payment/authAccessDetail",
            success:function(data){
                if(data.code==1){
                    //认证数趋势图
                    line1X=data.result.CertPerTrend.time;
                    line1Y=data.result.CertPerTrend.PeopleSum;
                    console.log(line1X,line1Y);
                    //认证流量趋势图区块
                    line2X=data.result.CertFlowTrend.Time;
                    line2Y=data.result.CertFlowTrend.FlowSum;
                    console.log(line2X,line2Y);
                    // //默认加载的区块
                    accessOption = lineCharts('rgba(0, 222, 255,1)','rgba(0, 222, 255,0.2)',line1X,line1Y,"");
                    var access = echarts.init(document.getElementById('access'));
                    access.setOption(accessOption);
                    //认证方式
                    var RZproductPercent=data.result.RZCertTRMLper;
                    var _RZarray=[];
                    var _RZarrayName=[];

                    for(var index in RZproductPercent){
                        _RZarray.push({value:RZproductPercent[index].PeopleSum,name:RZproductPercent[index].TRML});
                        _RZarrayName.push(RZproductPercent[index].TRML);
                        // var _index=Number(index)+1;
                        // $(".productNum").eq(index).text(_index);
                        // $(".productNo").eq(index).text(productPercent[index].TRML);
                        // $(".productSell").eq(index).text(productPercent[index].PeopleSum);
                        // $(".productPercent").eq(index).text(productPercent[index].TRMLper);

                    }
                    //认证终端占比
                    var productPercent=data.result.CertTRMLper;
                    var _array=[];
                    var _arrayName=[];
                    for(var index in productPercent){
                        var _index=Number(index)+1;
                        $(".productNum").eq(index).text(_index);
                        $(".productNo").eq(index).text(productPercent[index].TRML);
                        $(".productSell").eq(index).text(productPercent[index].PeopleSum);
                        $(".productPercent").eq(index).text(productPercent[index].TRMLper);
                        _array.push({value:productPercent[index].PeopleSum,name:productPercent[index].TRML});
                        _arrayName.push(productPercent[index].TRML);
                    }


                    //认证方式饼图
                    Authentication = pieChar(_RZarray,_RZarrayName);
                    var AuthenticationTrend = echarts.init(document.getElementById('Authentication'));
                    AuthenticationTrend.setOption(Authentication);

                    //认证终端占比
                    identify = pieChar(_array,_arrayName);
                    var identifyTrend = echarts.init(document.getElementById('identify'));
                    identifyTrend.setOption(identify);



                }else{
                    console.log(data.msg);
                }
            },
            error:function(){

            }
        });

    }
    ajaxFun(shopId,timeA,timeB);
    //商家ID获取
    $.ajax({
        type:"post",
        dataType:"json",
        url:"/payment/business/getAllBusiness",
        success:function(data){
            var bussinessIdValue=data.result.business;
            console.log(bussinessIdValue);
            for(var i=1;i<bussinessIdValue.length;i++){
            	$(".select").append('<option value='+bussinessIdValue[i].id+'>'+bussinessIdValue[i].name+'</option>');
            };
            $(".select").val(shopId);
        },
        error:function(){
            console.log("请求失败");
        }
    });

    //更改商家ID
    $(".select").change(function(){
        shopId=$('.select option:selected').val();
        var People = document.getElementById("people");
        var Flow = document.getElementById("flow");
        var flowCss = $(Flow).hasClass("myButton");
        if(flowCss){
            $(Flow).removeClass("myButton");
            $(People).addClass("myButton");
        }else{
            $(People).addClass("myButton");
        };
        console.log(shopId);
        ajaxFun(shopId,timeA ,timeB);
    });

    //更改时间
    //时间选择

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
            ajaxFun(shopId,timeA,timeB);

        }
        return argsMap;
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
});