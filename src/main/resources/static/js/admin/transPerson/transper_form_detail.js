(function () {
    function createDelete() {
        var span = document.createElement('span');
        span.innerHTML = 'x';
        span.setAttribute('style','margin-left:10px;color:red;font-weight:bold;cursor:pointer');
        span.setAttribute('class','delete');
        return span;
    }
    function deletefj() {
        var deletes = document.getElementsByClassName('delete');
        for(var i=0;i<deletes.length;i++) {
            deletes[i].onclick = function () {
                ids.splice(ids.indexOf(this.id),1);
                this.parentNode.setAttribute('style','display:none;');
            }
        }
    }

    var ids = [];
    var s1 = createDelete();
    var fjlbLi = $('#fjlb li');
    fjlbLi.append(s1);
    for (var i=0;i<fjlbLi.length;i++) {
        ids.push(fjlbLi[i].id);
    }
    deletefj();


    $('#uploadFile').click(function () {
        DatatableTool.modalShow("#upload-modal", "#fileUploadForm");
        var uploader = $("#fileUploadForm").FileUpload({
            url: "transper/upTransPerAtta",
            isMultiFile: true
        });
        uploader.done(function(data) {
            ids.push(data.result.id);
            var li = document.createElement('li');
            var s2 = createDelete();
            s2.setAttribute('id',data.result.id);
            li.innerHTML = data.result.name;
            li.appendChild(s2);
            $('#fjlb').append(li);
            deletefj();
        });
    });

    // //附件查看
    // var n = 1;
    // $('#fjck').click(
    //     function () {
    //         var attachList = JSON.parse($('#attachList').val());
    //         var contractId = $('#contractId').val();
    //         if(attachList.length == 0) {
    //             alert('无附件');
    //         } else {
    //             if(n%2==1) {
    //                 for (var i = 0; i < attachList.length; i++) {
    //                     var li = document.createElement('li');
    //                     var div = document.createElement('div');
    //                     div.innerHTML = attachList[i].name;
    //                     div.setAttribute('style', 'cursor:pointer;');
    //                     div.setAttribute('class', 'attachment');
    //                     li.appendChild(div);
    //                     li.setAttribute('class', 'attList')
    //                     this.parentNode.appendChild(li);
    //                     div.id = attachList[i].id;              //将变量保存给对象,避免循环闭包
    //                     div.onclick = function () {
    //                         // window.location = "contract/contractAttachmentDownload?attachmentId=" + this.id+"&contractId="+contractId;
    //                         window.location = "attachment/download?id=" + this.id;
    //                     }
    //                 }
    //             }
    //             else {
    //                 $('.attList').hide();
    //             }
    //             n += 1;
    //         }
    //     }
    // );
    //

    //提交
    $('#tj').click(function() {
        var formContents = document.getElementsByClassName('form-content');
        for(var i=0; i<formContents.length; i++ ) {
            if(/^\s*$/.test(formContents[i].value)) {
                alert('请填写完整信息!');
                return false;
            }
        }
        AjaxTool.post('transper/addAndCom', $('#t_apply_form').serialize()+"&bczl="+this.id+"&ids="+ids, function (data) {
                alert(data.message);
                toSqjl();
            }
        )
    });

    function toSqjl() {
        AjaxTool.getHtml('transper/getApplyTables',function (html) {
            $('.page-content').html(html);
        });
    }


    $('#back').click(function () {
        var tabId = ($('#back').data('tabId'));
        AjaxTool.getHtml('transper/getApplyTables',function (html) {
            $('.page-content').html(html);
            // $('#'+tabId).trigger('click');
        });
    });

})();



//
// //这个js版本不支持箭头函数，所以无法获取vue的实例，我只能退而求其次，曲线救国
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
//         get_form: function (me) {
//             AjaxTool.post('transper/getNotCom',{},function (data) {
//                 console.log(data)
//             });
//         },
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
//         resetForm: function (me) {
//             me.ruleForm = {staffNum: '', toDepartment: '', jobKind: '', date: '', number: 1, reason:'', ids:[]}
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
//         resetForm_hock: function () {
//             this.resetForm(this)
//         }
//     },
//     watch: {
//         message: function (msg) {
//             // alert(msg)
//             console.log(msg)
//         }
//     }
// })