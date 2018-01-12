/**
 * Created by ly on 2017/7/11.
 */
function lineCharts(topAreaCol,botAreaCol,Xdata,Ydata,UnitName)
{
  return  {
      	tooltip: {
            trigger: 'axis',
            position: function (pt) {
                return [pt[0], '10%'];
            }
            },
         dataZoom: [{
          show: true,
          realtime: true,
          start: 0,
          end: 100,
          xAxisIndex: [0, 1],
          borderColor:'rgba(255,255,255,0.3)',
          borderWidth:1,
          handleStyle: {
          	color: 'rgba(255,255,255,0.3)',
            shadowBlur: 3,
            shadowColor: 'rgba(0, 0, 0, 0.6)',
            shadowOffsetX: 1,
            shadowOffsetY: 1
          },
          textStyle:{
          	color:"#ffffff"
          	}
      }, {
          type: 'inside',
          realtime: true,
          start: 0,
          end: 100,
          xAxisIndex: [0, 1],
          
      }],
      grid: [{
          left: 40,
          right: 30,
          top:15,
          bottom:65
      }, {
          left: 40,
          right: 30,
          top:15,
          bottom:65
      }],
      xAxis: [{
          type: 'category',
          boundaryGap: false,
          axisLine:{
     			show:true,
                lineStyle: { 
                	color: 'rgba(255,255,255,0.2)'
                	}
              },
            axisTick : { show: false },
            splitLine: {
              show: true,
              lineStyle:{
                color:'rgba(255,255,255,0.1)'
              }
            },
            axisLabel :{
                textStyle :
                  {
                    show: true,
                    color : 'rgba(255,255,255,0.8)'
                  }
            },
          data: Xdata
      }, {
          gridIndex: 1
      }],

      yAxis: [{
          type: 'value',
          axisLine:{
     			show:true,
                lineStyle: { 
                	color: 'rgba(255,255,255,0.2)'
                	}
              },
          axisTick : { show: false },
          splitLine: {
                show: true,
                lineStyle:{
                	color:'rgba(255,255,255,0.1)'
                }
            },
          axisLabel :{
                textStyle :
                  {
                    show: true,
                    color : 'rgba(255,255,255,0.8)'
                  }
            },

      }, {
          gridIndex: 1
      }],
      series: [{
          name:UnitName,
          type: 'line',
          smooth: true,
          lineStyle: {
              normal: {
                  width: 1,
                   color: topAreaCol
              }
          },
          itemStyle:{
          	normal: {
                        color: topAreaCol
                    }
          },
          areaStyle: {
          	normal: {
          		color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
          			offset: 0,
          			color: topAreaCol
          		}, {
          			offset: 1,
          			color: botAreaCol
          			}])
          		}
          },
           data:Ydata

      }]
  };
}