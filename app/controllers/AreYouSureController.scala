/*
 * Copyright 2026 HM Revenue & Customs
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

import config.FrontendAppConfig
import config.annotations.EstateRegistration
import controllers.actions.Actions
import forms.YesNoFormProvider
import models.requests.DataRequest
import navigation.Navigator
import pages.HaveUTRYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ChangeUTRYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AreYouSureController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      @EstateRegistration navigator: Navigator,
                                      actions: Actions,
                                      formProvider: YesNoFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      changeUTRYesNoView: ChangeUTRYesNoView,
                                      config: FrontendAppConfig
                                    )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider.withPrefix("sureForUTR")

  def checkUTRForSure(origin: Option[String]): Action[AnyContent] = actions() { implicit request =>

    val preparedForm = request.userAnswers.get(HaveUTRYesNoPage) match {
      case None => form
      case Some(value) => form.fill(value)
    }

    Ok(changeUTRYesNoView(preparedForm, isOrgCredUser))

  }

  private def actions(): ActionBuilder[DataRequest, AnyContent] = actions.authWithData

  private def isOrgCredUser(implicit request: DataRequest[AnyContent]): Boolean =
    request.affinityGroup == Organisation

  def onSubmit(): Action[AnyContent] =
    actions().async { implicit request =>

      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(
                changeUTRYesNoView(
                  formWithErrors,
                  isOrgCredUser
                )
              )
            ),

          value =>
            for {
              updatedAnswers <- Future.fromTry(
                request.userAnswers.set(
                  HaveUTRYesNoPage,
                  value
                )
              )

              _ <- sessionRepository.set(updatedAnswers)

            } yield {
              if (value) {
                Redirect(
                  navigator.nextPage(
                    HaveUTRYesNoPage,
                    updatedAnswers
                  )
                )
              } else {
                Redirect(
                  s"${config.suitabilityUrl}?origin=checkyourAnswers"
                )
              }
            }
        )
    }

}
