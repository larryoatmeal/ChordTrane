package controllers

import play.api._
import play.api.mvc._
import models.website._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.libs.Files
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints

object Authentication extends Controller{ 


  //Forms
  val userForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText.verifying(Constraints.pattern("""\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}\b""".r, error="Not a valid email address")),
      "password" -> nonEmptyText
    )
    ((firstName, lastName, email, password) => User(-1, email, password, firstName, lastName))
    (user => Some(user.firstName, user.lastName, user.email, user.password))
  )

  def submitSignup = Action{
    implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.signup(formWithErrors)),
      value => {
        User.addUser(value)
        Redirect(routes.Application.home())
      }
    )
  }

  def signupPage = Action{
    implicit request => 
    Ok(views.html.signup(userForm))
  }

  def loginPage = Action{
    implicit request => 
    Ok(views.html.login(userForm))
  }

  def logout = Action{
    implicit request => 
    Redirect(routes.Application.home).withNewSession
  }

  val loginForm = Form(
    mapping(
      "email" -> nonEmptyText.verifying(Constraints.pattern("""\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}\b""".r, error="Not a valid email address")),
      "password" -> nonEmptyText
    )
    ((email, password) => User(-1, email, password, "", ""))//firstName, lastName unused
    (user => Some(user.email, user.password)).verifying(
      u => !User.checkCredentials(u.email, u.password).isEmpty//check if valid
    )
  )

  def submitLogin = Action{
    implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.login(formWithErrors)),
      value => {
        val userId = User.checkCredentials(value.email, value.password).get //must exist if passed validation. Redundant, fix
        Redirect(routes.Application.home()).withSession(
          "id" -> userId.toString
          )
      }
    )
  }

  



}