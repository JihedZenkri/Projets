(* Module Print - printing Polish programs *)

open Types

(* Turns an op to its string form *)
let string_of_op op = match op with Add -> "+" | Sub -> "-" | Mul -> "*" | Div -> "/" | Mod -> "%"

(* Turns an expr to its string form *)
let rec string_of_expr expr =
  match expr with
  | Num a -> string_of_int a
  | Var name -> name
  | Op (op, expr1, expr2) -> string_of_op op ^ " " ^ string_of_expr expr1 ^ " " ^ string_of_expr expr2

(* Turns a comp to its string form *)
let string_of_comp comp =
  match comp with
  | Eq -> "="
  | Ne -> "<>"
  | Lt -> "<"
  | Le -> "<="
  | Gt -> ">"
  | Ge -> ">="

(* Turns a cond to its string form *)
let string_of_cond cond =
  match cond with
  | expr1, cmp, expr2 -> string_of_expr expr1 ^ " " ^ string_of_comp cmp ^ " " ^ string_of_expr expr2

let rec repeat ch n = if n = 0 then "" else ch ^ repeat ch (n - 1)

let block_is_empty (block' : block) = block' = []

let indent_line d = repeat "  " d

(* Turns an instr to its string form *)
let rec string_of_instr instr d =
  match instr with
  | Set (name, value) -> name ^ " := " ^ string_of_expr value
  | Read name -> "READ " ^ name
  | Print expr -> "PRINT " ^ string_of_expr expr
  | If (cond, block1, block2) -> (
      if not (block_is_empty block2) then
        string_of_if_else cond block1 block2 d
      else
        string_of_if cond block1 d
    )
  | While (cond, block) -> "WHILE " ^ string_of_cond cond ^ "\n" ^ string_of_block block (d + 1)

and string_of_block block d =
  match block with
  | (i, instr) :: block' -> indent_line d ^ string_of_instr instr d ^
      (if not (block_is_empty block') then
        "\n" ^ string_of_block block' d
      else
        "")
  | [] -> ""

and string_of_if cond block1 d = "IF " ^ string_of_cond cond ^ "\n" ^ string_of_block block1 (d + 1)

and string_of_if_else cond block1 block2 d = string_of_if cond block1 d ^ "\n" ^ indent_line d ^ "ELSE\n" ^ string_of_block block2 (d + 1)

let print_polish (p : program) : unit = print_string (string_of_block p 0 ^ "\n")
