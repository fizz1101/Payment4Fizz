/**
 * Created by wushuang on 2017/7/12.
 */
function pieChar(value1,value2)
{
    return option = {
    	color:["#f0573d","#ffb400","#01becc","#9a76be","#ff7d2e"],
    	tooltip:{
    		show:true
    	},
        legend: {
            x : 'center',
            top:20,
            // data:['苹果','其它','OPPO','华为','vivo'],
            data:value2,
            textStyle:{
            	color:"rgba(255,255,255,0.8)"
            }
        },
        series : [
            {
                name: '认证终端占比',
                type: 'pie',
                center:["50%","55%"],
                radius: '70%',
                // roseType: 'angle',
                // data:[
                //     {value:235, name:'苹果'},
                //     {value:274, name:'其它'},
                //     {value:310, name:'OPPO'},
                //     {value:335, name:'华为'},
                //     {value:400, name:'vivo'}
                // ]
                data:value1
            }
        ]
    };
}