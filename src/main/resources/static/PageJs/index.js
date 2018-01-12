var data_index;
var colorList=["red-color","yellow-color","green-color","blue-color","blue-color"];

$(document).ready(function(){

    var bodyH = $("#content").height();
    var screenH = $(window).height();
    if (screenH > bodyH) {
        $("#content").css("height", screenH);
    }
    else {
        $("#content").css("height",bodyH);
    }
    var accessTrend = echarts.init(document.getElementById('accessTrend'));
    var flowTrend = echarts.init(document.getElementById('flowTrend'));
    ajaxCon();
    refreshs();
    function ajaxCon(){
        $.ajax({
            type:"post",
            dataType:"json",
            contentType: "application/json; charset=utf-8",
            url:"/payment/stat/indexData",
            success:function(data){

                if(data.code==1){
                	data_index = data.result;
                    $("#data").text(data.result.Date);
                    $("#week").text(data.result.Week);
                    $("#HistCert").text(data.result.HistCert);
                    $("#TodayCert").text(data.result.CertContrast.TodayCert);
                    $("#YesCert").text(data.result.CertContrast.YesCert);
                    perColor(data.result.CertContrast.PerCert,"#PerCert");

                    accessTrendOption = lineCharts('rgba(0, 222, 255,1)','rgba(0, 222, 255,0.2)',data.result.CertTrend.Time,data.result.CertTrend.PeopleSum,'认证人数');
                    accessTrend.setOption(accessTrendOption);

                    flowTrendOption = lineCharts('rgba(87, 202, 18,1','rgba(87, 202, 18,0.2)',data.result.FlowTrend.Time,data.result.FlowTrend.FlowSum,'流量');
                    flowTrend.setOption(flowTrendOption);

                    $("#shopName").text(data.result.CertTop[0].Shop);


                    ajaxConArea();
                    // if(data.result.CertTop.length>5){
                    //     for(var i=0;i<5;i++){
                    //         progress(i+1,data.result.CertTop[i],colorList[i]);
                    //     }
                    // }else{
                    //     for(var i=0;i<data.result.CertTop.length;i++){
                    //         progress(i+1,data.result.CertTop[i],colorList[i]);
                    //     }
                    // }

                    $("#TodayFlow").text(data.result.FlowContrast.TodayFlow);
                    $("#YesFlow").text(data.result.FlowContrast.YesFlow);
                    $("#TodayFlow2").text(data.result.FlowPeak.TodayFlow);
                    $("#YesFlow2").text(data.result.FlowPeak.YesFlow);

                    perColor(data.result.FlowContrast.PerFlow,"#PerFlow");

                    perColor(data.result.FlowPeak.PerFlow,'#PerFlow2');


                    ajaxConArea2();
                    // if(data.result.FlowTop.length>5){
                    //     for(var i=0;i<5;i++){
                    //         progress2(i+1,data.result.FlowTop[i],colorList[i]);
                    //     }
                    // }else{
                    //     for(var i=0;i<data.result.FlowTop.length;i++){
                    //         progress2(i+1,data.result.FlowTop[i],colorList[i]);
                    //     }
                    // }

                    $("#OnlineNow").text(data.result.OnlineNow);


                    if(data.result.OnlineTop.length>5){
                        for(var i=0;i<5;i++){
                            progress3(i+1,data.result.OnlineTop[i],colorList[i]);
                        }
                    }else{
                        for(var i=0;i<data.result.OnlineTop.length;i++){
                            progress3(i+1,data.result.OnlineTop[i],colorList[i]);
                        }
                    }


                    for(var i=0;i<data.result.OnlineTop.length;i++){
                        (function(i){
                            $(".onlineList").eq(i).click(function(){
                                window.location.href="pages/access.html?id="+data.result.OnlineTop[i].ShopId;
                            });
                        })(i);
                    }


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
    function refreshs()
    {
        function re()
        {
            $("#YesCert").empty();
            $("#PerCert").empty();
            $("#top").empty();
            $("#YesFlow").empty();
            $("#PerFlow").empty();
            $("#YesFlow2").empty();
            $("#PerFlow2").empty();
            $("#top2").empty();
            $("#OnlineTop").empty();
            ajaxCon();
            //ajaxConArea();
            //ajaxConArea2();
        };

        timer=null;
        // timer=setInterval(refreshTime,1000 * 60);
        timer=setInterval(re,300000);
    }
});
//TOP5认证 点位区域 数据 开始
//ajaxConArea();
function ajaxConArea(){
	if (data_index==null || data_index=='undefined') {
		$.ajax({
			type:"post",
			dataType:"json",
			contentType: "application/json; charset=utf-8",
			url:"/payment/stat/indexData",
			success:function(data){
				if(data.code==1){
					data_index = data.result;
					if(data.result.CertTop.length>5){
						for(var i=0;i<5;i++){
							progressArea(i+1,data.result.CertTop[i],colorList[i]);
						}
					}else{
						for(var i=0;i<data.result.CertTop.length;i++){
							progressArea(i+1,data.result.CertTop[i],colorList[i]);
						}
					}
				}else{
					console.log(data.msg);
				};
			},
			error:function() {
				alert("请求失败");
			}
			
		});
	} else {
		var CertTop = data_index.CertTop;
		if(CertTop.length>5){
			for(var i=0;i<5;i++){
				progressArea(i+1,CertTop[i],colorList[i]);
			}
		}else{
			for(var i=0;i<CertTop.length;i++){
				progressArea(i+1,CertTop[i],colorList[i]);
			}
		}
	}
}
function ajaxConPoint(){
	if (data_index==null || data_index=='undefined') {
	    $.ajax({
	        type:"post",
	        dataType:"json",
	        contentType: "application/json; charset=utf-8",
	        url:"/payment/stat/indexData",
	        success:function(data){
	            if(data.code==1){
	            	data_index = data.result;
	                if(data.result.CertPointTop.length>5){
	                    for(var i=0;i<5;i++){
	                        progressPoint(i+1,data.result.CertPointTop[i],colorList[i]);
	                    }
	                }else{
	                    for(var i=0;i<data.result.CertPointTop.length;i++){
	                        progressPoint(i+1,data.result.CertPointTop[i],colorList[i]);
	                    }
	                }
	            }else{
	                console.log(data.msg);
	            };
	        },
	        error:function() {
	            alert("请求失败");
	        }
	
	    });
	} else {
		var CertPointTop = data_index.CertPointTop;
		if(CertPointTop.length>5){
            for(var i=0;i<5;i++){
                progressPoint(i+1,CertPointTop[i],colorList[i]);
            }
        }else{
            for(var i=0;i<CertPointTop.length;i++){
                progressPoint(i+1,CertPointTop[i],colorList[i]);
            }
        }
	}
}
$("#point").click(function(){
    var area = document.getElementById("area");
    var point = document.getElementById("point");
    $('#top').empty();
    ajaxConPoint();
    $(point).addClass("myButton");
    var areaCss = $(area).hasClass("myButton");
    if(areaCss){
        $(area).removeClass("myButton");
        $(point).addClass("myButton");
    }else{
        $(point).addClass("myButton");
    };
});
$("#area").click(function(){
    $('#top').empty();
    ajaxConArea();
    var area = document.getElementById("area");
    var point = document.getElementById("point");
    var pointCss = $(point).hasClass("myButton");
    if(pointCss){
        $(point).removeClass("myButton");
        $(area).addClass("myButton");
    }else{
        $(area).addClass("myButton");
    };

});
function progressArea(i,data,colorList) {
//TOP5区域数据
    $("#top").append(
        '<div class="com-box row" style="margin-top: 25px; margin-left: 5px;">'+
        '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">'+
        '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 no-padding">'+
        '<span class="TopBox ' + colorList + '">'+i+'</span>'+
        '</div>'+
        '<span class="col-xs-9 col-sm-9 col-md-9 col-lg-9 no-padding cut">'+
        '<span class="shopName" id="shopName" title="'+data.Shop+'">'+data.Shop+'</span>'+
        '</span>'+
        '</div>'+
        '<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 progress no-padding">'+
        '<div class="progress-bar progress-bar-success ' + colorList +
        '" role="progressbar"aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"style="width: '+data.ShopPer+'%;" title="'+data.PeopleSum+'人">'+
        '</div>'+
        '</div>'+
        '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">'+
        '<div class="per">'+data.ShopPer+'%</div>'+
        '</div>'+
        '</div>'
    );
}
function progressPoint(i,data,colorList) {
//TOP5区域数据
    $("#top").append(
        '<div class="com-box row" style="margin-top: 25px; margin-left: 5px;">'+
        '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">'+
        '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 no-padding">'+
        '<span class="TopBox ' + colorList + '">'+i+'</span>'+
        '</div>'+
        '<span class="col-xs-9 col-sm-9 col-md-9 col-lg-9 no-padding cut">'+
        '<span class="shopName" id="shopName" title="'+data.Shop+'">'+data.Shop+'</span>'+
        '</span>'+
        '</div>'+
        '<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 progress no-padding">'+
        '<div class="progress-bar progress-bar-success ' + colorList +
        '" role="progressbar"aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"style="width: '+data.ShopPer+'%;" title="'+data.PeopleSum+'人">'+
        '</div>'+
        '</div>'+
        '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">'+
        '<div class="per">'+data.ShopPer+'%</div>'+
        '</div>'+
        '</div>'
    );
}
//TOP5认证 点位区域 数据 结束

//TOP5流量 点位区域 数据 开始
//ajaxConArea2();
function ajaxConArea2(){
	if (data_index==null || data_index=='undefined') {
	    $.ajax({
	        type:"post",
	        dataType:"json",
	        contentType: "application/json; charset=utf-8",
	        url:"/payment/stat/indexData",
	        success:function(data){
	            if(data.code==1){
	            	data_index = data.result;
	                if(data.result.FlowTop.length>5){
	                    for(var i=0;i<5;i++){
	                        progressArea2(i+1,data.result.FlowTop[i],colorList[i]);
	                    }
	                }else{
	                    for(var i=0;i<data.result.FlowTop.length;i++){
	                        progressArea2(i+1,data.result.FlowTop[i],colorList[i]);
	                    }
	                }
	            }else{
	                console.log(data.msg);
	            }
	        },
	        error:function() {
	            alert("请求失败");
	        }
	
	    });
	} else {
		var FlowTop = data_index.FlowTop;
		if(FlowTop.length>5){
            for(var i=0;i<5;i++){
                progressArea2(i+1,FlowTop[i],colorList[i]);
            }
        }else{
            for(var i=0;i<FlowTop.length;i++){
                progressArea2(i+1,FlowTop[i],colorList[i]);
            }
        }
	}
}
function ajaxConPoint2(){
	if (data_index==null || data_index=='undefined') {
	    $.ajax({
	        type:"post",
	        dataType:"json",
	        contentType: "application/json; charset=utf-8",
	        url:"/payment/stat/indexData",
	        success:function(data){
	            if(data.code==1){
	            	data_index = data_result;
	                if(data.result.FlowPointTop.length>5){
	                    for(var i=0;i<5;i++){
	                        progressPoint2(i+1,data.result.FlowPointTop[i],colorList[i]);
	                    }
	                }else{
	                    for(var i=0;i<data.result.FlowPointTop.length;i++){
	                        progressPoint2(i+1,data.result.FlowPointTop[i],colorList[i]);
	                    }
	                }
	            }else{
	                console.log(data.msg);
	            }
	        },
	        error:function() {
	            alert("请求失败");
	        }
	
	    });
	} else {
		var FlowPointTop = data_index.FlowPointTop;
		if(FlowPointTop.length>5){
            for(var i=0;i<5;i++){
                progressPoint2(i+1,FlowPointTop[i],colorList[i]);
            }
        }else{
            for(var i=0;i<FlowPointTop.length;i++){
                progressPoint2(i+1,FlowPointTop[i],colorList[i]);
            }
        }
	}
}
$("#point2").click(function(){
    var area = document.getElementById("area2");
    var point = document.getElementById("point2");
    $('#top2').empty();
    ajaxConPoint2();
    $(point).addClass("myButton");
    var areaCss = $(area).hasClass("myButton");
    if(areaCss){
        $(area).removeClass("myButton");
        $(point).addClass("myButton");
    }else{
        $(point).addClass("myButton");
    };
});
$("#area2").click(function(){
    $('#top2').empty();
    ajaxConArea2();
    var area = document.getElementById("area2");
    var point = document.getElementById("point2");
    var pointCss = $(point).hasClass("myButton");
    if(pointCss){
        $(point).removeClass("myButton");
        $(area).addClass("myButton");
    }else{
        $(area).addClass("myButton");
    };
});
function progressArea2(i,data,colorList) {

    $("#top2").append(
        '<div class="com-box row"  style="margin-top: 25px; margin-left: 5px;">'+
        '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">'+
        '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 no-padding">'+
        '<span class="TopBox ' + colorList + '">'+i+'</span>'+
        '</div>'+
        '<span class="col-xs-9 col-sm-9 col-md-9 col-lg-9 no-padding cut">'+
        '<span class="shopName" id="shopName" title="'+data.Shop+'">'+data.Shop+'</span>'+
        '</span>'+
        '</div>'+
        '<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 progress no-padding">'+
        '<div class="progress-bar progress-bar-success ' + colorList + '" role="progressbar"aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"style="width: '+data.ShopPer+'%;" title="'+data.FlowSum+'GB">'+
        '</div>'+
        '</div>'+
        '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">'+
        '<div class="per">'+data.ShopPer+'%</div>'+
        '</div>'+
        '</div>'
    );
}
function progressPoint2(i,data,colorList) {

    $("#top2").append(
        '<div class="com-box row"  style="margin-top: 25px; margin-left: 5px;">'+
        '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">'+
        '<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 no-padding">'+
        '<span class="TopBox ' + colorList + '">'+i+'</span>'+
        '</div>'+
        '<span class="col-xs-9 col-sm-9 col-md-9 col-lg-9 no-padding cut">'+
        '<span class="shopName" id="shopName" title="'+data.Shop+'">'+data.Shop+'</span>'+
        '</span>'+
        '</div>'+
        '<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 progress no-padding">'+
        '<div class="progress-bar progress-bar-success ' + colorList + '" role="progressbar"aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"style="width: '+data.ShopPer+'%;" title="'+data.FlowSum+'GB">'+
        '</div>'+
        '</div>'+
        '<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">'+
        '<div class="per">'+data.ShopPer+'%</div>'+
        '</div>'+
        '</div>'
    );
}
//TOP5流量 点位区域 数据 结束

function progress3(i,data,colorList) {
    if(i==5){
        $("#OnlineTop").append(
            '<div class="onlineList">'+
            '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 no-padding">'+
            '<span class="OnlineBox '+colorList+'">'+i+'</span>'+
            '</div>'+
            '<div class="col-xs-7 col-sm-7 col-md-7 col-lg-7 no-padding">'+
            '<p class="online_top_title cut" title="'+data.Shop+'">'+data.Shop+'</p>'+
            '<p class="online_top_data">'+data.PeopleSum+'<span>人</span></p>'+
            '</div>'+
            '<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 no-padding">'+
            '</div>'+
            '</div>'
        );
    }else {
        $("#OnlineTop").append(
            '<div class="onlineList">'+
            '<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 no-padding">'+
            '<span class="OnlineBox '+colorList+'">'+i+'</span>'+
            '</div>'+
            '<div class="col-xs-7 col-sm-7 col-md-7 col-lg-7 no-padding">'+
            '<p class="online_top_title cut" title="'+data.Shop+'">'+data.Shop+'</p>'+
            '<p class="online_top_data">'+data.PeopleSum+'<span>人</span></p>'+
            '</div>'+
            '<div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 no-padding">'+
            '<div class="online_line"></div>'+
            '</div>'+
            '</div>'
        );
    }
}

function perColor(per,perid){
    if(per == '∞'){
        $(perid).append(
            '<span class="text-per text-yes">'+per+'</span>'
        );
    }else{
        if(per>0){
            $(perid).append(
                '<span class="text-per text-red">'+per+'%↑</span>'
            );
        }else{
            if(per==0){
                $(perid).append(
                    '<span class="text-per">'+per+'</span>'
                );
            }else{
                $(perid).append(
                    '<span class="text-per text-green">'+per+'%↓</span>'
                );
            }
        }
    }
}

