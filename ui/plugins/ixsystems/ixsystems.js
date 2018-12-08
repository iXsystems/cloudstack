// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.




(function (cloudStack) {
    cloudStack.plugins.ixsystems = function(plugin) {
        /*
        var ixLogin = function(url, username, password){
            // Call Method to register the login credentials
        };

        var ixPrimaryDatastores = function(url, username, password){
            // Call Method to register the login credentials
        };


        var ixSecondaryDatastores = function(url, username, password){
            // Call Method to register the logi
*/

        cloudStack.ixsystemNodeWizard = {
            steps: [
                // Step 1: Setup
                function(args) {

                },
            ],
            action: function(args) {
                var deployVmData = {};
                console.log("Action @ wizard");
            },


        },
            cloudStack.uiCustom.ixsystemNodeWizard = function(args) {
                return function (listViewArgs) {
                    var context = listViewArgs.context;

                    var ixWizard = function (data) {

                        // Step 1 - Looking for FN
                        var step1Html = " <!-- Step 1: Setup End point-->\n" +
                            "                        <div class=\"step setup\" wizard-step-id=\"setup\">\n" +
                            "                            <div class=\"content\">\n" +
                            "                                <!-- Select a zone -->\n" +
                            "                                <div class=\"section select-zone\">\n" +
                            "                                    <img width=\"25%\" src=\"/client/plugins/ixsystems/TrueNAS_Z50.png\"/>\n"+
                            "                                </div>\n" +
                            "                                <!-- Select template -->\n" +
                            "                                <div class=\"section select-template\">\n" +
                            "                                    <h3><translate key=\"label.select.iso.or.template\" /></h3>\n" +
                            "                                    <p></p>\n" +
                            "                                    <div class=\"select-area\">\n" +
                            "                                        <div class=\"desc\"><translate key=\"message.template.desc\"/></div>\n" +
                            "                                        <input type=\"radio\" name=\"select-template\" value=\"select-template\" />\n" +
                            "                                        <label><translate key=\"label.template\"/></label>\n" +
                            "                                    </div>\n" +
                            "                                    <div class=\"select-area\">\n" +
                            "                                        <div class=\"desc\"><translate key=\"message.iso.desc\"/></div>\n" +
                            "                                        <input type=\"radio\" name=\"select-template\" value=\"select-iso\" />\n" +
                            "                                        <label>ISO</label>\n" +
                            "                                    </div>\n" +
                            "                                </div>\n" +
                            "                            </div>\n" +
                            "                        </div>";

                        var step2Html = " <!-- Step 1: Setup End point-->\n" +
                            "                        <div class=\"step setup\" wizard-step-id=\"setup\">\n" +
                            "                            <div class=\"content\">\n" +
                            "                                <!-- Select a zone -->\n" +
                            "                                <div class=\"section select-zone\">\n" +
                            "                                    <img width=\"25%\" src=\"/client/plugins/ixsystems/TrueNAS_Z50.png\"/>\n"+
                            "                                </div>\n" +
                            "                                <!-- Select template -->\n" +
                            "                                <div class=\"section select-template\">\n" +
                            "                                    <h3><translate key=\"label.select.iso.or.template\" /></h3>\n" +
                            "                                    <p></p>\n" +
                            "                                    <div class=\"select-area\">\n" +
                            "                                        <div class=\"desc\"><translate key=\"message.template.desc\"/></div>\n" +
                            "                                        <input type=\"radio\" name=\"select-template\" value=\"select-template\" />\n" +
                            "                                        <label><translate key=\"label.template\"/></label>\n" +
                            "                                    </div>\n" +
                            "                                    <div class=\"select-area\">\n" +
                            "                                        <div class=\"desc\"><translate key=\"message.iso.desc\"/></div>\n" +
                            "                                        <input type=\"radio\" name=\"select-template\" value=\"select-iso\" />\n" +
                            "                                        <label>ISO</label>\n" +
                            "                                    </div>\n" +
                            "                                </div>\n" +
                            "                            </div>\n" +
                            "                        </div>";


                        var template = '' +
                            '<div class="multi-wizard ixsystems-wizard">\n' +
                            '                <div class="progress">\n' +
                            '                    <ul>\n' +
                            '                        <li class="first active"><span class="number">1</span><span>Endpoint</span><span class="arrow"></span></li>\n' +
                            '                        <li><span class="number">2</span><span class="multiline">Credentials</span><span class="arrow"></span></li>\n' +
                            '                        <li><span class="number">3</span><span class="multiline">Primary Storage</span><span class="arrow"></span></li>\n' +
                            '                        <li><span class="number">4</span><span class="multiline">Secondary Storage</span><span class="arrow"></span></li>\n' +
                            '                        <li><span class="number">5</span><span>Review and finish</span><span class="arrow"></span></li>\n' +
                            '                    </ul>\n' +
                            '                </div>\n' +
                            '                <form>\n' +
                            '                    <div class="steps">'+step1Html+'</div></form>';

                        $('#template').html(template);
                        //console.log(template);
                        //console.log( $('#template'));
                        //console.log($('#template').find('div.ixsystems-wizard'));
                        var $wizard = $('#template').find('div.ixsystems-wizard').clone();
                        //var $wizard = $('#template').find('div.instance-wizard').clone();
                        var $progress = $wizard.find('div.progress ul li');
                        var $progress2 = $wizard.find('div.progress');
                        var $steps = $wizard.find('div.steps').children().show();
                        var $diagramParts = $wizard.find('div.diagram').children().show();
                        var $form = $wizard.find('form');
                        var $img = '<img width="25%" src="/client/plugins/ixsystems/TrueNAS_Z50.png"/>';
                        //$progress2.append($img);

                        $title = $wizard.find('.ui-dialog-title').html('Add TrueNAS node');

                        // Close instance wizard
                        var close = function() {
                            $wizard.dialog('destroy');
                            $('div.overlay').fadeOut(function() {
                                $('div.overlay').remove();
                            });
                        };


                        var stepActive = function(stepNumber){
                              // Change the active Step!
                        };

                        var changeStep = function(stepNumber){

                        };

                        console.log(args);
                        args.action({
                            // Populate data
                            context: context,
                            data: data,
                            $wizard: $wizard,
                            response: {
                                success: function(args) {

                                    close();
                                },
                                error: function(message) {
                                    $wizard.remove();
                                    $('div.overlay').remove();

                                    if (message) {
                                        cloudStack.dialog.notice({
                                            message: message
                                        });
                                    }
                                }
                            }
                        });

                        $form.validate();


                        return $wizard.dialog({
                            title: 'Add TrueNAS node',
                            width: 896,
                            height: 570,
                            closeOnEscape: false,
                            zIndex: 5000
                        })
                            .closest('.ui-dialog').overlay();
                    };
                    return ixWizard(args);
                }
            };

        plugin.ui.addSection({
            id: 'ixsystems',
            title: 'iXsystems',
            showOnNavigation: true,
            preFilter: function(args) {
                return isAdmin();
            },
            /*show: function() {
                return $('<div>').html('Test plugin section');
            },*/
            // Render page as a list view

            sectionSelect: {
                label: 'label.select-view',
                preFilter: function(args) {
                    if (isAdmin())
                        return ['listSystems', 'showSummary'];
                    else
                        return  ['listSystems', 'showSummary'];
                }
            },

            sections: {
                listSystems: {
                    type: 'select',
                    //title: 'label.ixsystems.listSystems',
                    title: 'iX Nodes',
                    listView: {
                        //id: 'testPluginInstances',
                        fields: {
                            hostname: { label: 'label.name' },
                            status: { label: 'label.status' },
                            state: {
                                label: 'label.metrics.state',
                                converter: function (str) {
                                    // For localization
                                    return str;
                                },
                                indicator: {
                                    'Running': 'on',
                                    'Stopped': 'off',
                                    'Error': 'off',
                                    'Destroyed': 'off',
                                    'Expunging': 'off',
                                    'Stopping': 'warning',
                                    'Shutdowned': 'warning'
                                }
                            },
                            details: { label: 'label.details' },
                        },
                        dataProvider: function(args) {
                            args.response.success({ data: [

                                    {hostname: 'truenas01.luis.ix.lab',
                                        status: 'UP', state: 'on',  'details': 'show' },
                                    {hostname: 'truenas02.luis.ix.lab',
                                        status: 'DOWN', state: 'warning', 'details': 'show' },
                                    {hostname: 'truenas03.luis.ix.lab',
                                        status: 'UP', state: 'on', 'details': 'show' },
                                    {hostname: '192.168.100.14',
                                        status: 'UP', state: 'on',  'details': 'show' },

                                ] });
                        },
                        actions: {
                            // The key/ID you specify here will determine what icon is shown in the UI for this action,
                            // and will be added as a CSS class to the action's element
                            // (i.e., '.action.restart')
                            //
                            // -- here, 'restart' is a predefined name in CloudStack that will automatically show
                            //    a 'reboot' arrow as an icon; this can be changed in csMyFirstPlugin.css
                            // Add instance wizard
                            add: {
                                label: 'Add iXsystems Node',

                                action: {
                                    custom: cloudStack.uiCustom.ixsystemNodeWizard(cloudStack.ixsystemNodeWizard)
                                },

                                messages: {
                                    notification: function(args) {
                                        return 'label.vm.add';
                                    }
                                },
                                notification: {
                                    poll: pollAsyncJobResult
                                }
                            },
                            restart: {
                                label: 'Restart VM',
                                messages: {
                                    confirm: function() { return 'Are you sure you want to restart this VM?' },
                                    notification: function() { return 'Rebooted VM' }
                                },
                                action: function(args) {
                                    // Get the instance object of the selected row from context
                                    //
                                    // -- all currently loaded state is stored in 'context' as objects,
                                    //    such as the selected list view row, the selected section, and active user
                                    //
                                    // -- for list view actions, the object's key will be the same as listView.id, specified above;
                                    //    always make sure you specify an 'id' for the listView, or else it will be 'undefined!'
                                    var instance = args.context.testPluginInstances[0];

                                    plugin.ui.apiCall('rebootVirtualMachine', {
                                        // These will be appended to the API request
                                        //
                                        // i.e., rebootVirtualMachine&id=...
                                        data: {
                                            id: instance.id
                                        },
                                        success: function(json) {
                                            args.response.success({
                                                // This is an async job, so success here only indicates that the job was initiated.
                                                //
                                                // To pass the job ID to the notification UI (for checking to see when action is completed),
                                                // '_custom: { jobID: ... }' needs to always be passed on success, in the same format as below
                                                _custom: { jobId: json.rebootvirtualmachineresponse.jobid }
                                            });
                                        },
                                        error: function(errorMessage) {
                                            args.response.error(errorMessage); // Cancel action, show error message returned
                                        }
                                    });
                                },

                                // Because rebootVirtualMachine is an async job, we need to add a poll function, which will
                                // perodically check the management server to see if the job is ready (via pollAsyncJobResult API call)
                                //
                                // The plugin API provides a helper function, 'plugin.ui.pollAsyncJob' which will work for most jobs
                                // in CloudStack
                                notification: {
                                    poll: plugin.ui.pollAsyncJob
                                }
                            }
                        },
                        detailView: {
                            name: 'label.alert.details',
                            actions: {

                                // Remove single Alert
                                remove: {
                                    label: 'label.delete',
                                    messages: {
                                        notification: function(args) {
                                            return 'label.alert.deleted';
                                        },
                                        confirm: function() {
                                            return 'message.confirm.delete.alert';
                                        }
                                    },
                                    action: function(args) {

                                        $.ajax({
                                            url: createURL("deleteAlerts&ids=" + args.context.alerts[0].id),
                                            success: function(json) {
                                                args.response.success();
                                                $(window).trigger('cloudStack.fullRefresh');
                                            }
                                        });

                                    }
                                },

                                archive: {
                                    label: 'label.archive',
                                    messages: {
                                        notification: function(args) {
                                            return 'label.alert.archived';
                                        },
                                        confirm: function() {
                                            return 'message.confirm.archive.alert';
                                        }
                                    },
                                    action: function(args) {

                                        $.ajax({
                                            url: createURL("archiveAlerts&ids=" + args.context.alerts[0].id),
                                            success: function(json) {
                                                args.response.success();
                                                $(window).trigger('cloudStack.fullRefresh');
                                            }
                                        });
                                    }
                                }

                            },

                            tabs: {
                                details: {
                                    title: 'label.details',
                                    fields: [{
                                        id: {
                                            label: 'label.id'
                                        },
                                        description: {
                                            label: 'label.description'
                                        },
                                        sent: {
                                            label: 'label.date',
                                            converter: cloudStack.converters.toLocalDate
                                        }
                                    }],
                                    dataProvider: function(args) {
                                        $.ajax({
                                            url: createURL("listAlerts&id=" + args.context.alerts[0].id),
                                            dataType: "json",
                                            async: true,
                                            success: function(json) {
                                                var item = json.listalertsresponse.alert[0];
                                                args.response.success({
                                                    data: item
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        },
                    },

                },
                showSummary: {

                    type: 'select',
                    //title: 'label.ixsystems.showSummary',
                    title: 'Manage iXsystems Storage',
                    show: function() {
                        return $('<div>').html('Test plugin section');
                    },
                    listView: {
                        //id: 'testPluginInstances',
                        fields: {
                            hostname: { label: 'label.name' },
                            status: { label: 'label.status' },
                            state: {
                                label: 'label.metrics.state',
                                converter: function (str) {
                                    // For localization
                                    return str;
                                },
                                indicator: {
                                    'Running': 'on',
                                    'Stopped': 'off',
                                    'Error': 'off',
                                    'Destroyed': 'off',
                                    'Expunging': 'off',
                                    'Stopping': 'warning',
                                    'Shutdowned': 'warning'
                                }
                            },
                            details: { label: 'label.details' },
                        },
                        dataProvider: function(args) {
                            args.response.success({ data: [

                                    {hostname: 'cenas',
                                        status: 'UP', state: 'on',  'details': 'show' },
                                    {hostname: 'cenas2',
                                        status: 'DOWN', state: 'warning', 'details': 'show' },
                                    {hostname: 'truenas03.luis.ix.lab',
                                        status: 'UP', state: 'on', 'details': 'show' },

                                ] });
                        },
                        actions: {
                            // The key/ID you specify here will determine what icon is shown in the UI for this action,
                            // and will be added as a CSS class to the action's element
                            // (i.e., '.action.restart')
                            //
                            // -- here, 'restart' is a predefined name in CloudStack that will automatically show
                            //    a 'reboot' arrow as an icon; this can be changed in csMyFirstPlugin.css
                            // Add instance wizard
                            add: {
                                label: 'Add iXsystems Node',

                                action: {
                                    custom: cloudStack.uiCustom.ixsystemNodeWizard(cloudStack.instanceWizard)
                                },

                                messages: {
                                    notification: function(args) {
                                        return 'label.vm.add';
                                    }
                                },
                                notification: {
                                    poll: pollAsyncJobResult
                                }
                            },
                            restart: {
                                label: 'Restart VM',
                                messages: {
                                    confirm: function() { return 'Are you sure you want to restart this VM?' },
                                    notification: function() { return 'Rebooted VM' }
                                },
                                action: function(args) {
                                    // Get the instance object of the selected row from context
                                    //
                                    // -- all currently loaded state is stored in 'context' as objects,
                                    //    such as the selected list view row, the selected section, and active user
                                    //
                                    // -- for list view actions, the object's key will be the same as listView.id, specified above;
                                    //    always make sure you specify an 'id' for the listView, or else it will be 'undefined!'
                                    var instance = args.context.testPluginInstances[0];

                                    plugin.ui.apiCall('rebootVirtualMachine', {
                                        // These will be appended to the API request
                                        //
                                        // i.e., rebootVirtualMachine&id=...
                                        data: {
                                            id: instance.id
                                        },
                                        success: function(json) {
                                            args.response.success({
                                                // This is an async job, so success here only indicates that the job was initiated.
                                                //
                                                // To pass the job ID to the notification UI (for checking to see when action is completed),
                                                // '_custom: { jobID: ... }' needs to always be passed on success, in the same format as below
                                                _custom: { jobId: json.rebootvirtualmachineresponse.jobid }
                                            });
                                        },
                                        error: function(errorMessage) {
                                            args.response.error(errorMessage); // Cancel action, show error message returned
                                        }
                                    });
                                },

                                // Because rebootVirtualMachine is an async job, we need to add a poll function, which will
                                // perodically check the management server to see if the job is ready (via pollAsyncJobResult API call)
                                //
                                // The plugin API provides a helper function, 'plugin.ui.pollAsyncJob' which will work for most jobs
                                // in CloudStack
                                notification: {
                                    poll: plugin.ui.pollAsyncJob
                                }
                            }
                        },
                    }


                }
            },



        });

    };


}(cloudStack));