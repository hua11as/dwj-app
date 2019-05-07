$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/order/list',
        datatype: "json",
        colModel: [
            { label: '期数', name: 'periods', width: 60, key: true },
            { label: '用户id', name: 'vipId', width: 60 },
            { label: '用户昵称', name: 'nickName', width: 60 },
            { label: '下注金额（元）', name: 'buyAmount', width: 60 },
            { label: '下注方式', name: 'selectedSize', width: 60, formatter: function(value, options, row){
                var array = ['闲','闲对','和','庄对','庄'];
                return array[value-1];
            }},
            { label: '下注结果', name: 'finalResult', width: 60 , formatter: function(value, options, row){
                    var arry = ['--','赢','输'];
                    return arry[value];
                }},
            { label: '创建时间', name: 'addTime', width: 60 }
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
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
        postData: {vipId: getParams("vipId")},
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
            nickName: '',
            periods: ''
        },
        showList: true,
        title: null,
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
                postData:{'vipId': vm.q.vipId, 'nickName': vm.q.nickName, 'periods': vm.q.periods},
                page:page
            }).trigger("reloadGrid");
        },
        drawRecord: function() {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            window.location.href = 'draw_record.html?awardPeriod='+id;
        }
    }
});