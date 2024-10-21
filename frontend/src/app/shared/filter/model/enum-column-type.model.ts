import {OptionViewModel} from './option-view.model'

export class EnumColumnTypeModel {
  values: string[]
  keys: string[]

  constructor(values: string[], keys: string[]) {
    this.values = values
    this.keys = keys
  }

  static fromOptionViews(optionViews: OptionViewModel[]): EnumColumnTypeModel {
    const values = optionViews.map((optionView) => optionView.name)
    return new EnumColumnTypeModel(values, values)
  }
}
