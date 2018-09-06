$(document).ready(function () {
    var ids = [];
    $('#uploadFile').click(function () {
        DatatableTool.modalShow("#upload-modal", "#fileUploadForm");
        var uploader = $("#fileUploadForm").FileUpload({
            url: "transper/upTransPerAtta",
            isMultiFile: true
        });
        uploader.done(function(data) {
            console.log(data)
            ids.push(data.result.id);
            var li = document.createElement('li');
            var span = document.createElement('span');
            span.innerHTML = 'x';
            span.setAttribute('style','margin-left:10px;color:red;font-weight:bold;cursor:pointer');
            span.setAttribute('class','delete');
            span.setAttribute('id',data.result.id);
            li.innerHTML = data.result.name;
            li.appendChild(span);
            $('#fjlb').append(li);

            var deletes = document.getElementsByClassName('delete');
            for(var i=0;i<deletes.length;i++) {
                deletes[i].onclick = function () {
                    ids.splice(ids.indexOf(this.id),1);
                    this.parentNode.setAttribute('style','display:none;');
                }
            }
        });
    });



    var buttons = document.getElementById('saveCon').getElementsByTagName('button');
    for(var i=0; i<buttons.length;i++) {
        buttons[i].onclick = function() {
            var formContents = document.getElementsByClassName('form-content');
            for(var i=0; i<formContents.length; i++ ) {
                if(/^\s*$/.test(formContents[i].value)) {
                    sweetAlert('请填写完整信息!');
                    return false;
                }
            }
            if(validator.form()) {
                AjaxTool.post('transper/addAndCom', $('#t_apply_form').serialize()+"&bczl="+this.id+"&ids="+ids, function (data) {
                        sweetAlert(data.message);
                        if(data.success) toSqjl();
                    }
                )
            }
        };
    }

    function toSqjl() {
        AjaxTool.getHtml('transper/getApplyTables',function (html) {
            $('.page-content').html(html);
        });
    }



    $('.date-picker').datetimepicker({
        format:'yyyy/mm/dd',
        language: 'zh-CN',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0
    });

    var validator = $('#t_apply_form').validate({
        errorElement: 'span', //default input error message container
        errorClass: 'error-tips', // default input error message class
        rules: {
            staffNum: {
                maxlength: 15
            },
            toDepartment: {
                maxlength: 10
            },
            jobKind: {
                maxlength: 10
            },
            number :{
                maxlength: 10
            },
            reason:{
                maxlength: 300
            },
            startDate: {
                dateISO: true
            },
            endDate: {
                dateISO: true
            }
        },
        messages: {

            staffNum: {
                maxlength: "不超过15个字"
            },
            toDepartment: {
                maxlength: "不超过10个字"
            },
            jobKind: {
                maxlength: "不超过10个字"
            },
            number :{
                maxlength: "不超过10个字"
            },
            reason:{
                maxlength: "不超过100个字"
            },
            startDate: {
                dateISO: "日期格式不正确"
            },
            endDate: {
                dateISO: "日期格式不正确"
            }
        },
        errorPlacement: function(error, element) { //错误信息位置设置方法
            if (element.is(':radio') || element.is(':checkbox')) { //如果是radio或checkbox
                var eid = element.attr('name'); //获取元素的name属性
                error.appendTo(element.parent().parent()); //将错误信息添加当前元素的父结点后面
            } else if(element.attr('name')=='zgs'){
                error.appendTo(element.parent())
            } else {
                error.insertAfter(element);
            }
        }
    });
});



// new Vue({
//     el: '#chostrans',
//     data: {
//         ruleForm: {
//             staffNum: '',
//             toDepartment: '',
//             jobKind: '',
//             date: '',
//             number: 1,
//             reason:'',
//             ids:[]
//         },
//         ids:[],
//         rules: {
//             staffNum: [
//                 { required: true, message: '请输入职工工号', trigger: 'blur' },
//                 { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
//             ],
//             toDepartment: [
//                 { required: true, message: '请输入派往单位', trigger: 'blur' },
//                 { min: 1, max: 15, message: '长度在 1 到 15 个字符', trigger: 'blur' }
//             ],
//             jobKind: [
//                 { required: true, message: '请输入工种', trigger: 'blur' },
//                 { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' }
//             ],
//             number: [
//                 { required: true, message: '请输入借调人数', trigger: 'blur' }
//             ],
//             reason: [
//                 { required: true, message: '请输入借调原因', trigger: 'blur' },
//                 { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
//             ],
//         }
//     },
//     mounted: function () {
//
//     },
//     methods: {
//         chooseWf:function (me,id) {
//             AjaxTool.html('transper/getOneFormHtml',{id: id},function (html) {
//                 $('.portlet-body').html(html);
//             });
//         },
//         upload_file :function (me) {
//                 DatatableTool.modalShow("#upload-modal", "#fileUploadForm");
//                 var uploader = $("#fileUploadForm").FileUpload({
//                     url: "transper/upTransPerAtta",
//                     isMultiFile: true
//                 });
//                 uploader.done(function(data) {
//                     me.ruleForm.ids.push(data.result.id);
//                     var li = document.createElement('li');
//                     var span = document.createElement('span');
//                     span.innerHTML = 'x';
//                     li.setAttribute('style','margin-left:0px;');
//                     span.setAttribute('style','margin-left:10px;color:red;font-weight:bold;cursor:pointer');
//                     span.setAttribute('class','delete');
//                     span.setAttribute('id',data.result.id);
//                     li.innerHTML = data.result.name;
//                     li.appendChild(span);
//                     $('#fjlb').append(li);
//
//                     var deletes = document.getElementsByClassName('delete');
//                     for(var i=0;i<deletes.length;i++) {
//                         deletes[i].onclick = function () {
//                             me.me.ruleForm.ids.splice(me.me.ruleForm.ids.indexOf(this.id),1);
//                             console.log(this.id)
//                             this.parentNode.setAttribute('style','display:none;');
//                         }
//                     }
//                 });
//         },
//         submitForm: function (me) {
//           console.log(me.ruleForm)
//             AjaxTool.post('transper/addAndCom',{form: JSON.stringify(me.ruleForm)},function (data) {
//                console.log(data)
//             });
//         },
//         storeForm: function (me) {
//             AjaxTool.post('transper/addNotCom',{form: JSON.stringify(me.ruleForm)},function (data) {
//                 console.log(data)
//                 if (data.success){
//                     Toast.show("保存成功")
//                     me.ruleForm = {staffNum: '', toDepartment: '', jobKind: '', date: '', number: 1, reason:'', ids:[]}
//                 }
//             });
//         },
//         chooseWf_hock: function (id) {
//             this.dele(this,id)
//         },
//         upload_hock: function () {
//             this.upload_file(this)
//         },
//         submitForm_hock:function () {
//             this.submitForm(this)
//         },
//         storeForm_hock: function () {
//             this.storeForm(this)
//         }
//     },
//     watch: {
//         message: function (msg) {
//             // alert(msg)
//             console.log(msg)
//         }
//     }
// })