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
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.HaveUTRYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HaveUTRYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @EstateRegistration navigator: Navigator,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: HaveUTRYesNoView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val queryParmaValue = "suitability-check-your-answers"

  private def actions(): ActionBuilder[DataRequest, AnyContent] = actions.authWithData

  val form: Form[Boolean] = formProvider.withPrefix("haveUtrYesNo")

  def onPageLoad(origin: Option[String]): Action[AnyContent] = actions() { implicit request =>
    val preparedForm = request.userAnswers.get(HaveUTRYesNoPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, isOrgCredUser, origin))
  }

  def onSubmit(origin: Option[String]): Action[AnyContent] = actions().async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              view(
                formWithErrors,
                isOrgCredUser,
                origin
              )
            )
          ),
        value =>
          for {
            updatedAnswers <-
              Future.fromTry(
                request.userAnswers.set(
                  HaveUTRYesNoPage,
                  value
                )
              )

            _ <- sessionRepository.set(updatedAnswers)

          } yield {

            val nextRoute: Call =
              origin match {

                case Some(originValue) if originValue == queryParmaValue =>
                  routes.AreYouSureController
                    .onPageLoad()

                case Some(_) =>
                  controllers.routes.SessionExpiredController.onPageLoad

                case None =>
                  navigator.nextPage(
                    HaveUTRYesNoPage,
                    updatedAnswers
                  )
              }

            Redirect(nextRoute)
          }
      )
  }

  private def isOrgCredUser(implicit request: DataRequest[AnyContent]): Boolean =
    request.affinityGroup == Organisation

}
