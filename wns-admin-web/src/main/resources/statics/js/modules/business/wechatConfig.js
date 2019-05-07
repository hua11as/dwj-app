$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'wechat/config/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 30, key: true },
            { label: 'AppId', name: 'appId', sortable: false, width: 60 },
            { label: 'appSecret', name: 'appSecret', width: 60 },
            { label: 'desc', name: 'desc', width: 60 },
            { label: '用户状态', name: 'status', width: 60, formatter: function(value, options, row){
                return value === 1 ?
                    '<span class="label label-success">正常</span>' :
                    '<span class="label label-danger">冻结</span>';
            }},
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
            appId: null
        },
        showList: true,
        title: null,
        config: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        update: function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            $.get(baseURL + "wechat/config/info/"+id, function(r){
                vm.showList = false;
                vm.title = "修改";
                vm.config = r.config;
            });
        },
        del: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "wechat/config/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(index){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function (event) {
            var url = vm.config.id == null ? "wechat/config/save" : "wechat/config/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.config),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'appId': vm.q.appId},
                page:page
            }).trigger("reloadGrid");
        }
    }
});