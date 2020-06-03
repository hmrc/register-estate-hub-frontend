/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import controllers.actions.{DataRequiredAction, DataRetrievalAction, IdentifierAction}
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.EstateRegisteredOnlinePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.EstateRegisteredOnlineView

import scala.concurrent.{ExecutionContext, Future}

class EstateRegisteredOnlineController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  sessionRepository: SessionRepository,
                                                  navigator: Navigator,
                                                  identify: IdentifierAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: YesNoFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: EstateRegisteredOnlineView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions() = identify andThen getData andThen requireData

  val form: Form[Boolean] = formProvider.withPrefix("estateRegisteredOnline")

  def onPageLoad(mode: Mode): Action[AnyContent] = actions() {
    implicit request =>

      val preparedForm = request.userAnswers.get(EstateRegisteredOnlinePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm))
  }

  def onSubmit(mode: Mode) = actions().async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EstateRegisteredOnlinePage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EstateRegisteredOnlinePage, mode, updatedAnswers))
        }
      )
  }
}
