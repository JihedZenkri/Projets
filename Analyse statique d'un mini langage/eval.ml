(* Module Eval - Polish program evaluation *)

open Types

(* Adds a couple (name, value) to env *)
let rec add_to_env name value env =
  match env with
  | [] -> [ (name, value) ]
  | (lname, lvalue) :: lenv ->
      if lname = name then (name, value) :: lenv
      else (lname, lvalue) :: add_to_env name value lenv

(* Evaluates binops +,-,*,/,% *)
let eval_op op =
  match op with
  | Add -> fun x y -> x + y
  | Sub -> fun x y -> x - y
  | Mul -> fun x y -> x * y
  | Div -> fun x y -> x / y
  | Mod -> fun x y -> x mod y

(* Evaluates comps >,>=,<,<=,<>,= *)
let eval_comp comp =
  match comp with
  | Eq -> fun x y -> x = y
  | Ne -> fun x y -> x <> y
  | Lt -> fun x y -> x < y
  | Le -> fun x y -> x <= y
  | Gt -> fun x y -> x > y
  | Ge -> fun x y -> x >= y

(* Evaluates an expr *)
let rec eval_expr expr env =
  match expr with
  | Num x -> x (* If expr is an int *)
  | Var s -> List.assoc s env (* If expr is a var *)
  | Op (op, expr1, expr2) -> (eval_op op) (eval_expr expr1 env) (eval_expr expr2 env) (* If expr is an operation *)

(* Evaluates a cond *)
let eval_cond cond env =
  match cond with
  | expr1, cmp, expr2 -> eval_comp cmp (eval_expr expr1 env) (eval_expr expr2 env)

(* Evaluates an instr *)
let rec eval_instr instr env =
  match instr with
  | Set (s, expr) -> let x = eval_expr expr env in add_to_env s x env
  | Read name -> print_string name; print_string "? "; let x = read_int () in add_to_env name x env
  | Print expr -> print_int (eval_expr expr env); print_newline (); env
  | If (cond, block1, block2) -> (
      if eval_cond cond env then 
        eval_block block1 env
      else 
        eval_block block2 env
    )
  | While (cond, block) -> (
      if eval_cond cond env then
        eval_instr (While (cond, block)) (eval_block block env)
      else env
    )

and eval_block block env =
  match block with
  | (i, instr) :: block' ->
      let env' = eval_instr instr env in
      eval_block block' env'
  | [] -> env

  let eval_polish (p : program) : unit =
    let env = eval_block p [] in
    ()