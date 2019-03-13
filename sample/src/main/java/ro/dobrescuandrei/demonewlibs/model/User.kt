package ro.dobrescuandrei.demonewlibs.model

import ro.dobrescuandrei.demonewlibs.model.utils.ID
import ro.dobrescuandrei.demonewlibs.model.utils.uuid

class User
(
    val id : ID = uuid(),
    val name : String
)
