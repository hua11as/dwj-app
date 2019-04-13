$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/count/list',
        datatype: "json",
        colModel: [
            { label: '统计日期', name: 'statDate', sortable: false,width: 60 },
            { label: 'VIP注册数量', name: 'registerNum',sortable: false, width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/vip_manager.html?createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '累计充值', name: 'recharge', sortable: false, width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/user_flowRecords.html?type=1&status=1&createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '累计提现', name: 'withdraw',sortable: false, width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/user_flowRecords.html?type=2&status=1&createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '累计下注', name: 'bet', sortable: false,width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/user_flowRecords.html?type=4&status=1&createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '累计赔付', name: 'compensate',sortable: false, width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/user_flowRecords.html?type=3&status=1&createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '累计佣金', name: 'commission', sortable: false,width: 60,
                formatter: function (value, options, row) {
                    const redirect_href = "window.location.href = 'modules/business/user_flowRecords.html?type=5&status=1&createTime="+ row.statDate +"';";
                    return '<a onclick="'+ redirect_href +'">'+ value +'</a>';
                }
            },
            { label: '平台余分', name: 'morePoints', sortable: false,width: 60},
            { label: '平台输赢', name: 'bunko', sortable: false,width: 60}
        ],
        height: '100%',
        rowNum: 1000,
        autowidth:true,
        multiselect: false,
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            vipId: null,
            type: 1,
        },
        showList: true,
        title: null,
	    typeData:[

	    ],
        charge: {}
    },
    methods: {
        query: function () {
            vm.reload();
        }
    }
});