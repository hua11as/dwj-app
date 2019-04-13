$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'user/charge/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 30, key: true },
            { label: '用户id', name: 'vipId', sortable: false, width: 60 },
            { label: '昵称', name: 'nickName', sortable: false, width: 60 },
            { label: '交易金额', name: 'amount', width: 60 },
            {label: '交易类型', name: 'type', sortable: false, width: 60,
                formatter: function (value, options, row) {
                    const isOnline = null != row.order_id;
                    return value === 1 ? (isOnline ? '充值（软件）' : '充值（手动）') : '提现';
            }},
            { label: '交易图片地址', name: 'image', width: 60 },
            { label: '交易日期', name: 'createTime', width: 100 }
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
        postData: {vipId: getParams("vipId"), type: getParams("type") || 1},
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

    let handle = getParams("handle");
    if ('charge' === handle) {
        vm.add();
    }
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            vipId: getParams("vipId"),
            nickName: null,
            type: getParams("type") || 1
        },
        showList: true,
        title: null,
	    typeData:[
		    {name:"充值",value: 1},
		    {name:"提现",value: 2}
	    ],
        charge: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.charge = {
                vipId: getParams("vipId"),
                type: getParams("type")
            };
            $("#file").val("");
            $("#imageShow").hide();
        },
        update: function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }

            $.get(baseURL + "user/charge/info/"+id, function(r){
                vm.showList = false;
                vm.title = "修改";
                vm.charge = r.charge;
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
                    url: baseURL + "user/charge/delete",
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
            var url = vm.charge.id == null ? "user/charge/save" : "user/charge/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.charge),
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
                postData:{'vipId': vm.q.vipId,"type": vm.q.type, 'nickName': vm.q.nickName},
                page:page
            }).trigger("reloadGrid");
        },
        upload: function (event) {
            var url = "file/upload";
            var formData = new FormData();
            var fileObj = document.getElementById("file").files[0];
            formData.append("file", fileObj);
            $.ajax({
                type: "POST",
                url: baseURL + url,
                // contentType: "application/json",
                data: formData,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function(r){
                    if(r.code === 0){
                        vm.charge.image = r.path;
                        $("#imageShow").attr("src", r.path);
                        $("#imageShow").show();
                    }else{
                        alert(r.msg);
                        $("#image").val("");
                        $("#imageShow").hide();
                    }
                }
            });
        }
    }
});