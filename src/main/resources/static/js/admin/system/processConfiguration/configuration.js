/**
 * Created by qifeng on 17/11/17.
 */
var count = 1;
var nodeName = null;
var allNodeName = [];
var allRoles;
var allNodeId=[];
var selectedRoleId=[];
var allProcesslb;


$(".add-button").click(function(){
    SweetAlert.swalInput("增加节点","请输入节点名称","",function(inputValue){
        if(allNodeName.length !=0 && isContains(inputValue)){
            Toast.show("对象添加失败",'节点描述重复');
            return;
        }
        var lclb = $("#lclb option:selected").text();
        if(lclb == "请选择"){
            Toast.show("请先选择流程类别");
            return;
        }
        $("#lclb").attr("disabled", "true");
        allNodeName.push(inputValue);

        var table = document.getElementById("node_table");

        var sel ="<select id='roleName"+(count)+"' th:multiple='true' >";
        for (var i=0;i<allRoles.length;i++){
            sel+="<option value='"+allRoles[i].id+"'>"+allRoles[i].name+"</option>"
        }
        sel+="</select>"
        oneRow = table.insertRow();//插入一行
        cell1= oneRow.insertCell();//单单插入一行是不管用的，需要插入单元格
        cell2=oneRow.insertCell();
        cell3 = oneRow.insertCell();
        cell1.innerHTML = count++;
        cell2.innerHTML=inputValue;
        cell3.innerHTML = sel;
    });
});
function isContains(inputValue) {
    for (var i=0;i<allNodeName.length;i++){
        if (allNodeName[i] == inputValue){
            return true;
        }
    }
    return false;
}
$('#node-tbody').on('click',function(e){
    var selectedTr = e.target;
    nodeName=selectedTr.innerHTML;

});

$(".update-button").click(function(){
    if(nodeName==null){
        Toast.show("对象提醒","请选择修改节点对象");
        return;
    }

    var nodeId = null;
    var i=0;
    if(Math.floor(nodeName) == nodeName){
        nodeId = nodeName;
    }
    else{
        for (;i<allNodeName.length;i++){
            if (allNodeName[i] == nodeName){
                nodeId = i+1;
                break;
            }
        }
    }
    SweetAlert.swalInput("修改节点对象","节点"+nodeId,"",function(inputValue){
        var table = document.getElementById("node_table");
        console.log(nodeId);
        table.rows[nodeId+1].cells[1].innerHTML = inputValue;
        allNodeName[i] = inputValue;
    });
});
$(".delete-button").click(function () {
    if (count == 1){
        return;
    }
    var table = document.getElementById("node_table");
    table.deleteRow(count);
    allNodeName.pop();
    count--;
    if(count == 1){
        $("#lclb").removeAttr("disabled");
    }

});
$(".save-button").click(function () {
    debugger;
    var processData=[];
    var table = document.getElementById("node_table");
    for(var i = 2, rows = table.rows.length; i < rows; i++){
        obj={};
        for(var j = 0, cells = table.rows[i].cells.length; j < cells; j++){
            if(j == 2){
                var roleId = $("#roleName"+(i-1)+" option:selected").val();
                obj.roleId= roleId;
            }else if(j==1){
                obj.nodeName = table.rows[i].cells[j].innerHTML;
            }else {

                obj.xh = table.rows[i].cells[j].innerHTML;
            }
        }
        processData.push(obj);
    }

    var formContents = document.getElementsByClassName('form-content');
    for(var i=0; i<formContents.length; i++ ) {
        if(/^\s*$/.test(formContents[i].value)) {
            alert('请填写完整信息!');
            return false;
        }
    }

    if(processData.length < 2){
        alert('流程节点配置不少于两个!');
    }else{
        $("#lclb").removeAttr("disabled");
        $.ajax({type:"post", url:"processConfiguration/save", data: $('#process_info').serialize() + "&processData="+JSON.stringify(processData),
            success:function(response){
                if(response.result){
                    Toast.show(response.message);
                    setTimeout('toLcjl()', 2000);
                }else{
                    $("#lclb").attr("disabled", "true");
                    Toast.show(response.message);
                }
            },
            error:function(){
                Toast.show("配置审批节点失败");
            }
        });
    }
});

function toLcjl(){
    AjaxTool.getHtml('processRecord/Record',function (html) {
        $('.page-content').html(html);
    });
}


$('.save-button2').click(function () {
    var a=$("#node_role_form").serializeArray();
    for (var i=0;i<a.length;i++){
        selectedRoleId.push(a[i].value);
    }

    AjaxTool.post("processConfiguration/bindNodeRole","&nodeId="+allNodeId+"&roleId="+selectedRoleId,function (response) {
        selectedRoleId = [];
        if (response.result){
            Toast.show("绑定节点和角色提醒",response.message);
        }
        else{
            Toast.show("绑定节点和角色失败",response.message);
        }
    });
});

$("#lclb").change(function(){
    var lclb = $("#lclb option:selected").text();
    getRoles(lclb);
});

function getRoles(lclb){
    $.ajax({type:"get", url:"processConfiguration/getRoles", data:"lclb=" + lclb, async: false,
        success:function(response) {
            if (response.result)
            {
                allRoles = response.obj;
            }
        },
        error:function(){
            alert("failed.");
        }
    });
}

$(document).ready(function() {

});
