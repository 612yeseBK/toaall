/**
 * Created by qifeng on 17/12/8.
 */

var processVoList;
var processDetail=null;
jQuery(document).ready(function() {
    init();
    // dataTableInit();
});

function closeLcb(lcid){
    var isConfir =confirm('您确认停用该流程吗?');
    if(isConfir==true){
        AjaxTool.post("processRecord/closeLcb", "&lcid="+lcid, function(response) {
            if(response.result){
                toLcjl();
                Toast.show(response.message);
            }else{
                Toast.show(response.message);
            }
        })
    }
}

function openLcb(lcid){
    var isConfir =confirm('您确认启用该流程吗?');
    if(isConfir==true){
        AjaxTool.post("processRecord/openLcb", "&lcid="+lcid, function(response) {
            if(response.result){
                toLcjl();
                Toast.show(response.message);
            }else{
                Toast.show(response.message);
            }
        })
    }
}

function toLcjl(){
    AjaxTool.getHtml('processRecord/Record',function (html) {
        $('.page-content').html(html);
    });
}
// function dataTableInit(){
//     AjaxTool.get("processRecord/getProcessVoList", null, function(data){
//         if(data.success){
//             var str = "";
//             var lcVos = data.content;
//             for(var i = 0; i < lcVos.length; i++){
//                 str += "<tr id = '"+lcVos[i].id+"'>";
//
//             }
//         }
//     })
// }

// function dataTableInit() {
    // AjaxTool.get("processRecord/getProcessVoList",null, function(response) {
    //     if (response.result){
    //         processVoList = response.obj;
    //         var str ="";
    //         if (processVoList.length >0){
    //
    //             var table = $('#process-record-table').DataTable();
    //             table.destroy();
    //
    //             $("#select-process").empty();
    //             for (var i=0;i<processVoList.length;i++){
    //                 $("#select-process").append("<option value='"+processVoList[i].processId+"'>"+processVoList[i].processId+"</option>");
    //             }
    //             var processVo = processVoList[0];
    //             processDetail = processVo.processDetail;
    //             for (var i=0;i<processDetail.length;i++){
    //                 var nodeName =" ";
    //                 nodeName = processDetail[i].nodeName;
    //                 console.log(nodeName);
    //                 var roleName=" ";
    //                 for (var j=0;j<processDetail[i].roleName.length;j++){
    //                     roleName = roleName+processDetail[i].roleName[j]+" ";
    //                 }
    //
    //                 str+="<tr>";
    //                 str+="<td>"+nodeName+"</td>";
    //                 str+="<td>"+roleName+"</td>";
    //                 str+="</tr>";
    //                 // t.row.add([nodeName,roleName]).draw(false)
    //             }
    //         }
    //         $("#process-record-tbody").html(str);
    //         init();
    //     }
    // });

// };
// $("#select-process").change(function () {
//     var processId = $("#select-process").val();
//     if (processVoList.length >processId-1){
//         //
//         // for (var i=0;i<table.rows.length;i++){
//         //     table.deleteRow(i);
//         // }
//
//         var processVo = processVoList[processId-1];
//         processDetail = processVo.processDetail;
//         var str ="";
//         for (var i=0;i<processDetail.length;i++){
//             var nodeName =" ";
//             nodeName = processDetail[i].nodeName;
//             console.log(nodeName);
//             var roleName=" ";
//             for (var j=0;j<processDetail[i].roleName.length;j++){
//                 roleName = roleName+processDetail[i].roleName[j]+" ";
//             }
//             str+="<tr>";
//             str+="<td>"+nodeName+"</td>";
//             str+="<td>"+roleName+"</td>";
//
//             str+="</tr>";
//         }
//         var table = $('#process-record-table').DataTable();
//         table.destroy();
//         $("#process-record-tbody").html(str);
//         init();
//
//     }
// });
// function init() {//dataTable初始化
//     mTable=DatatableTool.initDatatable("process-record-table", [ {
//         'orderable' : true,
//         'targets' : [0]
//     }]);
// }
// $("#delete").click(function () {
//     if (processDetail==null){
//         Toast.show("删除合同记录失败","不存在合同流程记录");
//         return;
//     }
//
//     if(!confirm("你确定删除吗")){
//         console.log("eferf");
//         return;
//     }
//     var processId = processDetail[0].processId;
//     AjaxTool.post("processRecord/deleteProcess","&processId="+processId,function (response) {
//         if (response.result){
//             Toast.show("删除合同审批流程成功");
//             dataTableInit();
//         }
//         else{
//             Toast.show("删除合同审批流程失败",response.message);
//         }
//     });
//
// });

function seeLcb(id){
    AjaxTool.html("processRecord/seeLcb", {id: id}, function(html){
        $('.portlet-body').html(html);
 //       $('#back').data('tabId', tabId);
    });
}


function init() {
    DatatableTool.initDatatable("process-record-table", [{
        'width': '30%',
        'targets': 0
    },
        {
            'orderable' : false,
            'targets' : [ 3 ]
        }, {
            "searchable" : false,
            "targets" : [ 3 ]
        }], [ [ 1, "desc" ] ]);
}

