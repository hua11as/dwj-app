$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + '/vip/user/relationList',
        datatype: "json",
        colModel: [
            { label: '关联级别', name: 'level', width: 60,
                formatter: function(value, options, row){
                    var values = ['下5级','下4级','下3级','下2级','下1级','','上1级','上2级','上3级','上4级','上5级'];
                    return values[value + 5];
                }
            },
            { label: '昵称', name: 'nickName', width: 60 },
            { label: 'VIP_ID', name: 'id', width: 30, key: true },
            { label: '手机号码', name: 'mobile', sortable: false, width: 60 },
            { label: '账号余额(元)', name: 'amount', width: 60 },
            { label: '累计充值(元)', name: 'recharge', width: 60 },
            { label: '累计提现(元)', name: 'withdraw', width: 60 },
            { label: '累计下注(元)', name: 'chipIn', width: 60 },
            { label: '累计赚取(元)', name: 'earn', width: 60 },
            { label: '累计佣金(元)', name: 'commission', width: 60 },
            // { label: '交易密码', name: 'password', width: 60 },
            { label: '用户状态', name: 'status', width: 60, formatter: function(value, options, row){
                return value === 0 ?
                    '<span class="label label-success">正常</span>' :
                    '<span class="label label-danger">冻结</span>';
            }}
        ],
        viewrecords: true,
        height: 385,
        // rowNum: 10,
        // rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        // multiselect: true,
        // pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        postData: {vipId: getParams("vipId"), level: getParams("level")},
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
            vipId: getParams("vipId"),
            level: getParams("level") || ''
        },
        showList: true,
        title: null,
        levels:[
            {name:"请选择关联等级",value: ""},
            {name:"上5级",value: 5},
            {name:"上4级",value: 4},
            {name:"上3级",value: 3},
            {name:"上2级",value: 2},
            {name:"上1级",value: 1},
            {name:"下1级",value: -1},
            {name:"下2级",value: -2},
            {name:"下3级",value: -3},
            {name:"下4级",value: -4},
            {name:"下5级",value: -5}
        ],
        user: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'vipId': vm.q.vipId, 'level' : vm.q.level},
                page:page
            }).trigger("reloadGrid");
        }
    }
});