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
            // Call Method to register the login credentials
        };
*/
        cloudStack.uiCustom.ixsystemNodeWizard = function(args) {
            return function (listViewArgs) {
                var context = listViewArgs.context;

                var ixWizard = function (data) {

                    var template = '' +
                        '<div class="multi-wizard ixsystems-wizard">\n' +
                        '                <div class="progress">\n' +
                        '                    <ul>\n' +
                        '                        <li class="first"><span class="number">1</span><span><translate key="label.setup"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">2</span><span class="multiline"><translate key="label.select.a.template"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">3</span><span class="multiline"><translate key="label.compute.offering"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">4</span><span class="multiline"><translate key="label.disk.offering"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">5</span><span><translate key="label.affinity"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">6</span><span><translate key="label.menu.network"/></span><span class="arrow"></span></li>\n' +
                        '                        <li><span class="number">7</span><span><translate key="label.menu.sshkeypair"/></span><span class="arrow"></span></li>\n' +
                        '                        <li class="last"><span class="number">8</span><span><translate key="label.review"/></span></li>\n' +
                        '                    </ul>\n' +
                        '                </div>\n' +
                        '                <form>\n' +
                        '                    <div class="steps"></div></form>';

                    var $wizard = $('#template').find('div.instance-wizard').clone();
                    var $progress = $wizard.find('div.progress ul li');
                    var $progress2 = $wizard.find('div.progress');
                    var $steps = $wizard.find('div.steps').children().hide();
                    var $diagramParts = $wizard.find('div.diagram').children().hide();
                    var $form = $wizard.find('form');
                    var $img = '<img width="25%" src="https://www.openstoragenas.com/images/TrueNAS/TrueNAS_Z50.png"/>';
                    $progress2.append($img);

                    $title = $wizard.find('.ui-dialog-title').html('Add TrueNAS node');

                    $form.validate();


                    return $wizard.dialog({
                        title: _l('label.vm.add'),
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
                      title: 'label.ixsystems.listSystems',
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
                      title: 'label.ixsystems.showSummary',
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