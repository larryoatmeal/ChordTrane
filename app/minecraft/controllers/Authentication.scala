package controllers.minecraft

import play.api._
import play.api.mvc._
import models.minecraft._
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
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
    ((username, password) => User(username, password, -1))
    (user => Some(user.username, user.password))
  )

  // def updateUser = Action{
  //   implicit request =>
  //   userForm.bindFromRequest.fold(
  //     formWithErrors => Ok(views.html.signup(formWithErrors)),
  //     value => {
  //       val userId = User.addUser(value)
  //       //When User created, add intial new song
        

  //       Redirect(routes.Application.home()).withSession(
  //         "minecraftUserName" -> 
  //       )
  //     }
  //   )
  // }

  // def signupPage = Action{
  //   implicit request => 
  //   Ok(views.html.signup(userForm))
  // }

  def loginPage = Action{
    implicit request => 
    Ok(minecraft.views.html.login(userForm))
  }

  // def logout = Action{
  //   implicit request => 
  //   Redirect(routes.Application.home).withNewSession
  // }

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
    ((username, password) => User(username, password, -1))
    (user => Some(user.username, user.password)).verifying("YOU SHALL NOT PASS", 
      u => User.checkCredentials(u.username, u.password)//check if valid
    )
  )

  def submitLogin = Action{
    implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Ok(minecraft.views.html.login(formWithErrors)),
      value => {
        Redirect(controllers.minecraft.routes.Application.home()).withSession(
          "minecraftid" -> value.id.toString
          )
      }
    )
  }


}