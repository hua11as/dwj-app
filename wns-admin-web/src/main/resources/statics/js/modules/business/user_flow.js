$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/record/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 30, key: true },
            { label: '用户id', name: 'vipId', sortable: false, width: 60 },
            { label: '用户昵称', name: 'nickName', width: 60 },
            { label: '交易金额', name: 'amount', width: 60 },
            {
                label: '交易类型', name: 'type', sortable: false, width: 60, formatter: function (value, options, row) {
                var array = ['充值','提现','赚入','下注','佣金','平台兑付','平台下注收入','平台佣金支出','提现申请','提现退回'];
                return array[value-1];
            }},
            { label: '交易状态', name: 'status', width: 60, formatter: function(value, options, row){
                return value === 0 ?
                    '<span class="label label-default">待入账</span>' : value === 1 ?
                    '<span class="label label-success">成功</span>' :
                    '<span class="label label-danger">失败</span>';
            }},
            { label: '交易日期', name: 'createTime', width: 100 },
            { label: '订单号(在线支付，才有订单号)', name: 'orderId', width: 100 }
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        // multiselect: true,
        pager: "#jqGridPager",
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
        postData: {vipId: getParams("vipId"), type: getParams("type"), status: getParams("status"), createTime: getParams("createTime")},
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

    laydate.render({
        elem: '#qCreateTime', //指定元素
        done: function(value, date, endDate){
            vm.q.createTime = value;
        }
    });
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            vipId: getParams("vipId"),
            nickName: "",
            type: getParams("type") || '',
            status: getParams("status") || '',
            createTime: getParams("createTime")
        },
        showList: true,
        title: null,
	    //type --> 1 充值   2，提现 3赚入，4下注，5佣金
	    typeData:[
            {name:"请选择交易类型",value: ""},
		    {name:"充值",value: 1},
		    {name:"提现",value: 2},
		    {name:"赚入",value: 3},
		    {name:"下注",value: 4},
		    {name:"佣金",value: 5},
            {name:"平台开奖兑付",value: 6},
            {name:"平台下注收入",value: 7},
            {name:"平台佣金支出",value: 8},
            {name:"提现申请",value: 9},
            {name:"提现退回",value: 10},
	    ],
	    jyStatus:[
            {name:"请选择交易状态",value: ""},
		    {name:"待入账",value: 0},
		    {name:"成功",value: 1},
		    {name:"失败",value: 2}
	    ],
        record: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'vipId': vm.q.vipId,'nickName': vm.q.nickName,"type": vm.q.type,"status": vm.q.status,"createTime": vm.q.createTime},
                page:page
            }).trigger("reloadGrid");
        }
    }
});