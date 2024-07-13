/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import client.scenes.*;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {
    /**
     * Configures bindings for controller classes using the provided binder.
     *
     * @param binder the binder to configure bindings with
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(AddParticipantCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AdminOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(DebtOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditExpenseTypeCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditTitleCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EventOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(InvitationCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LoginCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ManageExpenseCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StartScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LoginCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SettleDebtCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EventStatisticsCtrl.class).in(Scopes.SINGLETON);
    }
}