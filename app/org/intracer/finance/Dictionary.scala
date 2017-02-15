package org.intracer.finance

import client.finance.GrantItem

case class Dictionary(accountMap: Map[Int, Account] = Map.empty,
                      categoryMap: Map[Int, CategoryF] = Map.empty,
                      grantMap: Map[Int, Grant] = Map.empty,
                      grantItemMap: Map[Int, Seq[GrantItem]] = Map.empty,
                      projectsMap: Map[Int, Project] = Map.empty)

